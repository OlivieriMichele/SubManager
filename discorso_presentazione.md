# DISCORSO PRESENTAZIONE - SubManager
**Studente: Olivieri Michele | Corso: Programmazione Mobile**

---

## SLIDE 1-2: INTRODUZIONE E PROBLEMA

*"Il problema che ho voluto risolvere è molto concreto: nella vita quotidiana gestiamo numerosi abbonamenti - streaming come Netflix e Spotify, palestre, servizi software - che spesso dimentichiamo o sottovalutiamo dal punto di vista economico. Ci ritroviamo a pagare servizi che non usiamo più o a essere sorpresi dai rinnovi automatici."*

---

## SLIDE 3: SOLUZIONE - SubManager

*"SubManager permette di:"*
- **Tracciare tutti gli abbonamenti in un unico posto** - invece di dover controllare email o estratti conto
- **Monitorare spese mensili e annuali** - con calcoli automatici
- **Organizzare per categorie** - Intrattenimento, Fitness, Casa, Shopping
- **Visualizzare statistiche** - grafici a torta per capire dove vanno i soldi
- **Ricevere notifiche** - per i rinnovi imminenti, così da decidere se continuare o cancellare

---

## SLIDE 4: ARCHITETTURA - MVVM e Clean Architecture

*"L'app segue il pattern **MVVM (Model-View-ViewModel)** con principi di Clean Architecture."*

### **Model - Data Layer con Repository Pattern**

Il Data Layer è strutturato con il **Repository Pattern**, che astrae completamente le sorgenti dati dall'interfaccia.

**Esempio pratico con `SubscriptionRepository`:**

```kotlin
class SubscriptionRepository(
    private val subscriptionDao: SubscriptionDAOs,
    private val context: Context
) {
    val subscriptions: Flow<List<Subscription>> = subscriptionDao.getAllSubscriptions()
        .map { entities -> entities.map { it.toDomain() } }
    
    suspend fun addSubscription(subscription: Subscription) {
        subscriptionDao.insert(subscription.toEntity())
        // Pianifica automaticamente la notifica
        NotificationScheduler.scheduleNotification(context, subscription)
    }
}
```

**Perché usare il Repository Pattern?**
1. **Separazione delle responsabilità**: il ViewModel non sa se i dati vengono da Room, Firebase, o un API REST
2. **Testabilità**: posso sostituire il repository con un mock nei test
3. **Single source of truth**: tutta la logica di accesso ai dati è centralizzata
4. **Reattività**: espongo `Flow` invece di chiamate suspend, così l'UI si aggiorna automaticamente

Nel mio caso, `HomeViewModel` non tocca mai direttamente il database - chiama solo metodi del repository:

```kotlin
class HomeViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    val state: StateFlow<HomeState> = combine(
        subscriptionRepository.subscriptions,  // Flow dal repository
        subscriptionRepository.totalMonthly,
        subscriptionRepository.totalYearly,
        _filterName,
        _filterFunction
    ) { subscriptions, totalMonthly, totalYearly, filterName, filterFunction ->
        // Combino i dati e creo lo stato
        HomeState(
            subscriptions = filterFunction(subscriptions),
            totalMonthly = totalMonthly,
            totalYearly = totalYearly,
            // ...
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), HomeState(isLoading = true))
}
```

### **View - UI dichiarativa con Jetpack Compose**

La UI è completamente dichiarativa. In `HomeScreen.kt`:

```kotlin
@Composable
fun HomeScreen(
    state: HomeState,
    actions: HomeActions,
    onNavigateToCategories: () -> Unit,
    onSubscriptionClick: (Int) -> Unit,
    onNavigateToInsights: () -> Unit
) {
    LazyColumn {
        item { MainCard(state.totalMonthly, state.totalYearly) }
        item { StatsCards(state.subscriptionCount, state.expiringCount) }
        
        items(state.subscriptions) { sub ->
            SubscriptionItem(
                subscription = sub,
                onClick = { onSubscriptionClick(sub.id) }
            )
        }
    }
}
```

**Nessun findViewById, nessun adapter** - tutto è dichiarato come funzioni che descrivono l'interfaccia.

### **ViewModel - Gestione stato con StateFlow**

Il ViewModel espone lo **stato** tramite `StateFlow` e le **azioni** tramite un'interfaccia:

```kotlin
interface HomeActions {
    fun deleteSubscription(id: Int)
    fun showAll()
    fun showExpiring()
    fun sortByNameAsc()
    // ...
}
```

Questo pattern separa chiaramente:
- **State (cosa mostrare)** → `StateFlow<HomeState>`
- **Actions (cosa fare)** → interfaccia con metodi

La View osserva lo stato con `collectAsStateWithLifecycle()` e chiama le azioni quando l'utente interagisce.

**Clean Architecture:** separazione netta tra:
- **Presentation Layer** (UI/ViewModels)
- **Domain Layer** (modelli di dominio come `Subscription`, `Category`)
- **Data Layer** (Repository, Database, API)

---

## SLIDE 5: STACK TECNOLOGICO - UI/UX

### **Kotlin**
Linguaggio type-safe, con null safety integrato. Esempio:
```kotlin
val nextBilling: LocalDate  // Non può essere null
val description: String?    // Può essere null
```

### **Jetpack Compose**
Framework UI dichiarativo. Invece di XML, scrivo funzioni @Composable.

### **Material Design 3**
Utilizzo i nuovi componenti MD3:
- `MaterialTheme.colorScheme` per i colori adattivi
- `Card` con elevation dinamica
- `FloatingActionButton` con animazioni

### **Navigation Component**
Navigazione type-safe con **Kotlin Serialization**:

```kotlin
@Serializable data class ViewSubscription(val subscriptionId: Int) : Screen

navController.navigate(Screen.ViewSubscription(123))

// Nella destination:
composable<Screen.ViewSubscription> { backStackEntry ->
    val route = backStackEntry.toRoute<Screen.ViewSubscription>()
    // route.subscriptionId è type-safe!
}
```

---

## SLIDE 6: AUTENTICAZIONE - Firebase, Biometric e Keystore

### **Firebase Authentication**

Firebase gestisce l'autenticazione in modo sicuro. Nel mio `AuthRepository.kt`:

```kotlin
class AuthRepository(
    private val auth: FirebaseAuth,
    private val credentialsManager: SecureCredentialManager
) {
    suspend fun login(email: String, password: String, saveBiometric: Boolean): AuthResult {
        return try {
            // Chiamata Firebase - gestisce hash password, token JWT
            auth.signInWithEmailAndPassword(email, password).await()
            
            if (saveBiometric) {
                // Salva credenziali in modo sicuro per fingerprint
                credentialsManager.saveCredential(email, password)
                credentialsManager.setBiometricEnabled(true)
            }
            AuthResult(isSuccess = true)
        } catch (e: FirebaseAuthException) {
            AuthResult(isSuccess = false, errorMessage = mapError(e))
        }
    }
}
```

**Come funziona Firebase Authentication:**
1. L'utente inserisce email/password
2. `auth.signInWithEmailAndPassword()` invia le credenziali a Firebase
3. Firebase:
   - Verifica le credenziali contro il database cloud
   - Genera un **token JWT** (JSON Web Token)
   - Salva il token localmente (SharedPreferences criptate)
4. Nelle richieste successive, Firebase include automaticamente il token

**Perché usare Firebase Auth invece di un backend custom?**
- **Sicurezza**: password hashate con bcrypt, protezione CSRF
- **Scalabilità**: gestisce milioni di utenti
- **Funzionalità pronte**: reset password, verifica email, OAuth (Google, Facebook)

### **Integrazione Biometrica**

Il sistema biometrico permette di accedere con l'impronta digitale. In `BiometricAuthManager.kt`:

```kotlin
class BiometricAuthManager(private val activity: FragmentActivity) {
    
    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(activity)
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }
    
    fun authenticate(
        title: String = "Autenticazione",
        subtitle: String = "Verifica la tua identità",
        onResult: (BiometricResult) -> Unit
    ) {
        val biometricPrompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onResult(BiometricResult.Success)
                }
                override fun onAuthenticationFailed() {
                    onResult(BiometricResult.Failed)
                }
            }
        )
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("Annulla")
            .build()
            
        biometricPrompt.authenticate(promptInfo)
    }
}
```

**Flusso biometrico completo:**

1. **Setup iniziale** - L'utente fa login con email/password e attiva "Salva per fingerprint":
   ```kotlin
   if (saveBiometric) {
       credentialsManager.saveCredential(email, password)  // ← Android Keystore
       credentialsManager.setBiometricEnabled(true)        // ← DataStore
   }
   ```

2. **Salvataggio sicuro** - Le credenziali vanno in **Android Keystore**:
   ```kotlin
   class SecureCredentialManager(context: Context) {
       private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
       
       fun saveCredential(email: String, password: String) {
           // Genera chiave AES-256 dentro il Keystore hardware
           val secretKey = generateKey()
           
           // Cripta la password con AES
           val cipher = Cipher.getInstance("AES/GCM/NoPadding")
           cipher.init(Cipher.ENCRYPT_MODE, secretKey)
           val encryptedPassword = cipher.doFinal(password.toByteArray())
           
           // Salva in SharedPreferences (ma è criptata!)
           preferences.edit()
               .putString("email", email)
               .putString("encrypted_password", Base64.encodeToString(encryptedPassword, Base64.DEFAULT))
               .apply()
       }
   }
   ```

3. **Login successivo** - L'utente apre l'app e vede il pulsante fingerprint:
   ```kotlin
   // In LoginScreen
   if (authState.hasBiometric) {
       IconButton(onClick = { 
           biometricManager.authenticate { result ->
               when (result) {
                   BiometricResult.Success -> {
                       // Recupera credenziali dal Keystore
                       viewModel.loginWithBiometric()
                   }
               }
           }
       }) {
           Icon(Icons.Default.Fingerprint)
       }
   }
   ```

4. **Autenticazione e decriptazione**:
   ```kotlin
   suspend fun loginWithBiometric(): Result<Boolean> {
       // Recupera email
       val email = credentialsManager.getEmail()
       
       // Decripta password dal Keystore
       val password = credentialsManager.getPassword()  // ← decripta automaticamente
       
       // Login normale con Firebase
       auth.signInWithEmailAndPassword(email, password).await()
       return Result.success(true)
   }
   ```

### **Android Keystore: perché è sicuro**

Il **Keystore** è un container hardware-backed per chiavi crittografiche:

1. **Hardware Security Module (HSM)**: su dispositivi moderni, le chiavi sono generate e conservate in un chip separato (Trusted Execution Environment o Secure Element)
2. **Impossibile estrarre**: la chiave AES non può essere letta, nemmeno con root - può solo essere usata per criptare/decriptare
3. **Invalidazione automatica**: se l'utente rimuove il fingerprint dalle impostazioni, le chiavi vengono invalidate
4. **Protezione da attacchi**: anche se un malware legge le SharedPreferences, trova solo dati criptati - la chiave è nel chip hardware

**Differenza con SharedPreferences normali:**
- SharedPreferences normali: dati in chiaro → facilmente leggibili con adb o root
- Con Keystore: dati criptati + chiave in hardware → impossibile decriptare senza biometrico

---

## SLIDE 7: DEPENDENCY INJECTION - Koin

*"Per la Dependency Injection ho scelto **Koin**, un framework leggero e idiomatico per Kotlin."*

### **Perché Dependency Injection?**

Senza DI, i ViewModel creano le dipendenze manualmente:
```kotlin
// ❌ MALE: accoppiamento stretto
class HomeViewModel : ViewModel() {
    private val database = SubManagerDatabase.getDatabase(/* ??? context ??? */)
    private val dao = database.subscriptionDao()
    private val repository = SubscriptionRepository(dao)
}
```

**Problemi:**
- Come passo il Context?
- Impossibile testare (non posso sostituire il database con un mock)
- Ogni ViewModel deve sapere come costruire le dipendenze

**Con Koin:**
```kotlin
// ✅ BENE: dipendenze iniettate
class HomeViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel()
```

### **Configurazione in `appModule`**

```kotlin
val appModule = module {
    // Database (Singleton)
    single { SubManagerDatabase.getDatabase(androidContext()) }
    single { get<SubManagerDatabase>().subscriptionDao() }
    single { get<SubManagerDatabase>().categoryDao() }
    
    // Repository (Singleton)
    single { SubscriptionRepository(get(), androidContext()) }
    single { CategoryRepository(get()) }
    single { AuthRepository(get(), get()) }
    
    // ViewModel (Factory - nuova istanza ogni volta)
    viewModel { HomeViewModel(get(), get()) }
    viewModel { SubscriptionViewModel(get(), get()) }
}
```

**Inizializzazione in `SubManagerApplication.kt`:**
```kotlin
class SubManagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@SubManagerApplication)
            modules(appModule)  // ← Registra tutte le dipendenze
        }
    }
}
```

**Utilizzo nei Composable:**
```kotlin
@Composable
fun HomeScreen() {
    val homeViewModel = koinViewModel<HomeViewModel>()  // ← Koin inietta automaticamente
    val state by homeViewModel.state.collectAsStateWithLifecycle()
}
```

Koin risolve automaticamente il grafo delle dipendenze:
1. HomeViewModel richiede SubscriptionRepository
2. SubscriptionRepository richiede SubscriptionDao
3. SubscriptionDao richiede Database
4. Database richiede Context
5. Koin costruisce tutto nell'ordine giusto!

---

## SLIDE 8: PERSISTENZA DATI - Room, DataStore, Repository Pattern

### **Room Database**

Room è un ORM (Object-Relational Mapping) sopra SQLite. In `SubManagerDatabase.kt`:

```kotlin
@Database(
    entities = [SubscriptionEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SubManagerDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDAOs
    abstract fun categoryDao(): CategoryDAOs
}
```

**Entity esempio:**
```kotlin
@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val color: Int,
    val nextBillingDate: Long,  // LocalDate → epoch days
    @ColumnInfo(name = "category_name") val categoryName: String
)
```

**DAO con Flow reattivi:**
```kotlin
@Dao
interface SubscriptionDAOs {
    @Query("SELECT * FROM subscriptions ORDER BY nextBillingDate ASC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>
    
    @Query("SELECT SUM(price) FROM subscriptions")
    fun getTotalMonthly(): Flow<Double>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscription: SubscriptionEntity)
}
```

**Perché Flow invece di suspend?**
- `Flow` emette continuamente i dati aggiornati
- Quando inserisco un abbonamento, l'UI si aggiorna automaticamente
- Non serve chiamare `refresh()` o `loadData()` manualmente

**Prepopolamento database:**

Nel `DatabaseCallback` dentro `SubManagerDatabase.kt`:
```kotlin
private class DatabaseCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
            CoroutineScope(Dispatchers.IO).launch {
                prepopulateDatabase(database.categoryDao(), database.subscriptionDao())
            }
        }
    }
    
    private suspend fun prepopulateDatabase(
        categoryDAOs: CategoryDAOs,
        subscriptionDAOs: SubscriptionDAOs
    ) {
        // Inserisco categorie di default
        val defaultCategories = listOf(
            CategoryEntity("Intrattenimento", "Tv", 20.0, "", 0),
            CategoryEntity("Fitness", "FitnessCenter", 40.0, "", 2)
        )
        categoryDAOs.insertAll(defaultCategories)
        
        // Inserisco abbonamenti demo
        val subscriptions = listOf(
            SubscriptionEntity(0, "Netflix", 13.99, AccentColors.pastelPurple.toArgb(), 
                LocalDate.of(2026, 1, 15).toEpochDay(), "Intrattenimento"),
            // ...
        )
        subscriptionDAOs.insertAll(subscriptions)
    }
}
```

Questo codice viene eseguito **solo alla prima installazione**, creando dati demo per mostrare subito l'app popolata.

### **DataStore**

Per le preferenze (tema, notifiche attive, biometrico abilitato) uso **DataStore**, il successore di SharedPreferences:

```kotlin
class ThemePreferencesManager(context: Context) {
    private val dataStore = context.createDataStore(name = "theme_prefs")
    
    val themeFlow: Flow<Theme> = dataStore.data.map { prefs ->
        when (prefs[THEME_KEY]) {
            "dark" -> Theme.Dark
            "light" -> Theme.Light
            else -> Theme.System
        }
    }
    
    suspend fun setTheme(theme: Theme) {
        dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme.name.lowercase()
        }
    }
}
```

**Vantaggi di DataStore:**
- Type-safe con Kotlin Coroutines
- Transazioni atomiche (niente race conditions)
- Espone `Flow` invece di callback
- Gestisce errori di I/O automaticamente

### **Repository Pattern - Teoria**

Il Repository Pattern è un **ponte tra il dominio e la persistenza**:

```
ViewModel  →  Repository  →  [DAO, API, Cache]
             ↑
         Single source
         of truth
```

**Responsabilità del Repository:**
1. **Coordinamento dati**: combina più sorgenti (es. cache locale + API remota)
2. **Mappatura**: converte Entity → Domain models
3. **Business logic**: es. dopo inserimento, pianifica notifica
4. **Caching**: gestisce quando invalidare e aggiornare i dati

**Esempio nel mio `SubscriptionRepository`:**
```kotlin
val subscriptions: Flow<List<Subscription>> = subscriptionDao.getAllSubscriptions()
    .map { entities -> entities.map { it.toDomain() } }  // ← Mapping Entity → Domain

suspend fun addSubscription(subscription: Subscription) {
    subscriptionDao.insert(subscription.toEntity())  // ← Persist
    NotificationScheduler.scheduleNotification(context, subscription)  // ← Business logic
}
```

---

## SLIDE 9-11: SCREENS - Home, Categories, Subscription

### **Home Screen**

La Home mostra:
1. **MainCard**: spesa mensile e annuale totale
2. **StatsCards**: numero abbonamenti, abbonamenti in scadenza, categorie attive
3. **Lista abbonamenti** con filtri dinamici

**Filtri dinamici in `HomeViewModel`:**
```kotlin
private val _filterName = MutableStateFlow("Prossimi Rinnovi")
private val _filterFunction = MutableStateFlow<(List<Subscription>) -> List<Subscription>>({ it })

val state: StateFlow<HomeState> = combine(
    subscriptionRepository.subscriptions,
    _filterName,
    _filterFunction
) { subscriptions, filterName, filterFunction ->
    HomeState(
        subscriptions = filterFunction(subscriptions),  // ← Applica filtro
        currentFilterName = filterName
    )
}.stateIn(...)

// Azioni per cambiare filtro
override fun sortByPriceDesc() {
    _filterName.update { "Prezzo Decrescente" }
    _filterFunction.update { { subs -> subs.sortedByDescending { it.price } } }
}
```

**Come funziona:**
- `_filterFunction` contiene una lambda che trasforma la lista
- Quando l'utente clicca "Prezzo Decrescente", aggiorno la lambda
- `combine` ricalcola automaticamente lo stato
- L'UI si aggiorna grazie a `collectAsStateWithLifecycle()`

### **Categories Screen**

Mostra le categorie con budget e spesa corrente. 

**CategoryViewModel** usa lo stesso pattern:
```kotlin
val categoryListState = categoryRepository.categoriesWithStats.map { categories ->
    CategoryListState(categories = categories)
}.stateIn(...)
```

### **Subscription Screen**

Form generico riutilizzabile per **Create, View, Edit**:
```kotlin
@Composable
fun AddSubscriptionScreen(state: SubscriptionState, actions: SubscriptionActions) {
    // Stesso form, ma:
    // - se state.editingSubscription == null → MODE CREATE
    // - se state.editingSubscription != null → MODE EDIT
}
```

---

## SLIDE 12: INSIGHTS E SETTINGS

### **Insights - Data Visualization**

Grafico a torta per distribuzione spese per categoria, usando **Canvas API** di Compose:

```kotlin
Canvas(modifier = Modifier.size(200.dp)) {
    var startAngle = -90f
    categoryData.forEach { (category, percentage) ->
        val sweepAngle = percentage * 360f / 100f
        drawArc(
            color = category.color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = true
        )
        startAngle += sweepAngle
    }
}
```

### **Settings**

Toggle per:
- **Tema**: Dark/Light/System (salvato in DataStore)
- **Notifiche**: ON/OFF
- **Biometric**: abilita/disabilita fingerprint
- **Logout**: chiama `authRepository.logout()` e naviga a Login

---

## SLIDE 13: NOTIFICHE - Flusso Completo

*"Le notifiche sono la parte più complessa dell'app. Vediamo il flusso completo."*

### **1. INIZIALIZZAZIONE - SubManagerApplication**

All'avvio dell'app, in `SubManagerApplication.kt`:

```kotlin
override fun onCreate() {
    super.onCreate()
    
    // Crea il canale notifiche (obbligatorio Android 8.0+)
    NotificationHelper.createNotificationChannel(this)
    
    // Ripianifica tutte le notifiche esistenti
    applicationScope.launch {
        val subscriptions = subscriptionRepository.getAllSubscriptions()
        NotificationScheduler.rescheduleAllNotifications(this@SubManagerApplication, subscriptions)
    }
}
```

**Perché "reschedule all"?**
Quando Android termina l'app o il dispositivo si riavvia, WorkManager può perdere i task. Quindi:
- Al lancio, leggo tutti gli abbonamenti dal database
- Per ognuno, ricalcolo quando mandare la notifica
- Riprogrammo i `WorkRequest`

### **2. CREAZIONE ABBONAMENTO - SubscriptionRepository**

Quando l'utente crea un abbonamento:

```kotlin
suspend fun addSubscription(subscription: Subscription) {
    subscriptionDao.insert(subscription.toEntity())
    
    // Pianifica notifica automaticamente
    NotificationScheduler.scheduleNotification(context, subscription)
}
```

### **3. PIANIFICAZIONE - NotificationScheduler**

```kotlin
object NotificationScheduler {
    fun scheduleNotification(context: Context, subscription: Subscription) {
        val today = LocalDate.now()
        val renewalDate = subscription.nextBilling
        val daysUntilRenewal = ChronoUnit.DAYS.between(today, renewalDate)
        
        // Notifica 1 giorno prima del rinnovo
        if (daysUntilRenewal <= 0) return  // Già passato
        
        val delay = if (daysUntilRenewal == 1) {
            Duration.ofHours(0)  // Domani → notifica subito
        } else {
            Duration.ofDays(daysUntilRenewal - 1)
        }
        
        // Crea WorkRequest con delay
        val workRequest = OneTimeWorkRequestBuilder<SubscriptionReminderWorker>()
            .setInitialDelay(delay)
            .setInputData(workDataOf("subscription_id" to subscription.id))
            .addTag("subscription_${subscription.id}")
            .build()
        
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "reminder_${subscription.id}",
                ExistingWorkPolicy.REPLACE,  // Sostituisce se esiste già
                workRequest
            )
    }
}
```

**Cosa succede:**
1. Calcolo quanti giorni mancano al rinnovo
2. Se il rinnovo è domani, `delay = 0` (notifica oggi)
3. Se il rinnovo è tra 5 giorni, `delay = 4 giorni` (notifica tra 4 giorni)
4. Creo un `OneTimeWorkRequest` che eseguirà `SubscriptionReminderWorker`
5. Passo `subscription_id` come parametro
6. Registro in WorkManager con tag univoco

**WorkManager è persistente:**
- Sopravvive ai riavvii
- Rispetta Doze mode e battery optimization
- Ritenta automaticamente se fallisce

### **4. ESECUZIONE - SubscriptionReminderWorker**

Quando arriva il momento (1 giorno prima del rinnovo):

```kotlin
class SubscriptionReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val subscriptionId = inputData.getInt("subscription_id", -1)
        if (subscriptionId == -1) return Result.failure()
        
        // Leggo abbonamento dal database
        val subscription = subscriptionDao.getSubscriptionById(subscriptionId) ?: return Result.failure()
        
        // Calcolo giorni mancanti
        val daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), subscription.nextBilling)
        
        // Mostro notifica
        NotificationHelper.showSubscriptionReminderNotification(
            context = applicationContext,
            subscriptionId = subscription.id,
            subscriptionName = subscription.name,
            price = subscription.price,
            daysUntilRenewal = daysUntil.toInt()
        )
        
        return Result.success()
    }
}
```

**Perché rileggo dal database?**
L'abbonamento potrebbe essere stato modificato nel frattempo (prezzo cambiato, data spostata). Leggo i dati aggiornati per essere sicuro.

### **5. VISUALIZZAZIONE - NotificationHelper**

```kotlin
fun showSubscriptionReminderNotification(
    context: Context,
    subscriptionId: Int,
    subscriptionName: String,
    price: Double,
    daysUntilRenewal: Int
) {
    // Intent per aprire l'app al tap
    val intent = Intent(context, MainActivity::class.java).apply {
        action = ACTION_OPEN_SUBSCRIPTION
        putExtra(EXTRA_SUBSCRIPTION_ID, subscriptionId)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    
    val pendingIntent = PendingIntent.getActivity(
        context,
        subscriptionId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    
    // Costruisci notifica
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notification_sub)
        .setContentTitle("Rinnovo abbonamento")
        .setContentText("$subscriptionName si rinnova ${if (daysUntilRenewal == 0) "oggi" else "domani"} (€${String.format("%.2f", price)})")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()
    
    NotificationManagerCompat.from(context).notify(subscriptionId, notification)
}
```

### **6. TAP NOTIFICA - Deep Link**

Quando l'utente tocca la notifica, Android apre `MainActivity` con l'Intent:

**In `MainActivity.kt`:**
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handleNotificationIntent(intent)  // ← Gestisce intent iniziale
}

override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleNotificationIntent(intent)  // ← Gestisce intent mentre app è aperta
}

private fun handleNotificationIntent(intent: Intent?) {
    if (intent?.action == NotificationHelper.ACTION_OPEN_SUBSCRIPTION) {
        val subscriptionId = intent.getIntExtra(NotificationHelper.EXTRA_SUBSCRIPTION_ID, -1)
        if (subscriptionId != -1) {
            pendingSubscriptionId = subscriptionId  // ← Salva ID
        }
    }
}
```

**In `SubNavigation.kt`:**
```kotlin
var savedPendingId by remember { mutableStateOf<Int?>(null) }

// Salva l'ID quando arriva
LaunchedEffect(pendingSubscriptionId) {
    if (pendingSubscriptionId != null) {
        savedPendingId = pendingSubscriptionId
    }
}

// Naviga SOLO quando utente è autenticato
LaunchedEffect(authState.isAuthenticated, savedPendingId) {
    if (authState.isAuthenticated && savedPendingId != null) {
        delay(100)  // Aspetta che NavHost sia pronto
        navController.navigate(Screen.ViewSubscription(savedPendingId!!))
        savedPendingId = null
        onSubscriptionNavigated()
    }
}
```

**Flusso completo tap notifica:**
1. Utente tocca notifica → Android apre MainActivity
2. `handleNotificationIntent()` estrae `subscriptionId`
3. Se utente NON autenticato → mostra login, salva ID
4. Dopo login → `LaunchedEffect` rileva autenticazione
5. Naviga a `ViewSubscription(subscriptionId)`
6. Schermata dettaglio abbonamento si apre! 🎉

**Perché due LaunchedEffect?**
- Il primo salva l'ID immediatamente
- Il secondo aspetta che l'utente sia autenticato (potrebbe esserci biometric delay)
- Questo evita crash se si tenta di navigare prima che NavHost sia pronto

---

## SLIDE 14: REPOSITORY

*"Il repository su GitHub contiene tutta la documentazione e gli screenshot dell'app."*

**Link**: [github.com/OlivieriMichele/SubManager](https://github.com/OlivieriMichele/SubManager)

*"Sono disponibili anche i mockup originali in Figma che ho usato come riferimento per il design."*

---

## DOMANDE TECNICHE ANTICIPATE

### **"Perché hai scelto Koin invece di Dagger/Hilt?"**

*"Koin è più semplice e idiomatico per Kotlin. Non richiede annotation processing, quindi compile time più veloce. Per un progetto di questa dimensione, la differenza di performance runtime è trascurabile, ma Koin ha meno boilerplate."*

### **"Come gestisci la cancellazione delle notifiche se l'utente elimina un abbonamento?"**

*"Nel `SubscriptionRepository.deleteSubscription()` chiamo `NotificationScheduler.cancelNotification()`, che usa WorkManager per cancellare il task pianificato tramite il tag univoco."*

```kotlin
suspend fun deleteSubscription(id: Int) {
    subscriptionDao.delete(id)
    WorkManager.getInstance(context).cancelUniqueWork("reminder_$id")
}
```

### **"Cosa succede se cambio la data di rinnovo di un abbonamento?"**

*"Quando aggiorno un abbonamento, chiamo nuovamente `scheduleNotification()` con `ExistingWorkPolicy.REPLACE`, che sostituisce il WorkRequest esistente con uno nuovo con il delay aggiornato."*

### **"Il biometrico funziona su tutti i dispositivi?"**

*"No, verifico prima con `BiometricManager.canAuthenticate()`. Se il dispositivo non ha sensori biometrici o l'utente non ha configurato fingerprint, il pulsante non viene mostrato."*

### **"Come gestisci gli errori di rete con Firebase?"**

*"Firebase SDK gestisce automaticamente retry e offline mode. Se la rete cade durante il login, Firebase lancia `FirebaseAuthException` che catturo e mappo a messaggi user-friendly nel `AuthRepository.mapError()`."*

---

## CONCLUSIONE

*"In conclusione, SubManager dimostra l'applicazione di pattern moderni Android come MVVM, Clean Architecture, Dependency Injection con Koin, persistenza reattiva con Room e Flow, autenticazione sicura con Firebase e Keystore, e scheduling avanzato con WorkManager."*

*"L'app è completamente funzionante con tutte le feature richieste: persistenza dati, autenticazione biometrica, notifiche push, grafici, filtri dinamici, e navigation type-safe."*

*"Grazie per l'attenzione."*
