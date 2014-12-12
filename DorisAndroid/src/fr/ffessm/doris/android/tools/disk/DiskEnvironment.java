package fr.ffessm.doris.android.tools.disk;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.text.TextUtils.SimpleStringSplitter;
import android.util.Log;
import fr.ffessm.doris.android.tools.disk.StorageHelper.StorageVolume;
/**
*
* Hilfsklasse mit mehreren Funktionsblöcken, die auf die Problematik eingehen,
* dass viele moderne Android-Geräte technisch zwei SD-Karten haben:
* <ul>
* <li>Der "alte" Speicher, den Android als "External Memory" oder unter
* "mnt/sdcard" anspricht, ist dabei fest eingebaut; er hat zwar "sd" im Namen
* und wird über Methoden mit "external" im Namen angesprochen, aber
* das stimmt ansich nicht: Dieser Speicher ist nicht wechselbar und technisch
* nicht als echte physische SD-Karte implementiert.
* <li>Der bei diesen Geräten zugängliche Slot für eine microSD-Karte wird
* nicht über diese External-Methoden angesprochen. Bis Android 4.1 gibt es
* keinen offiziellen Weg, drauf zuzugreifen. Daher liegt bei diesen Smartphones
* die SD-Karte für Apps weitgehend brach.
* </ul>
* <p>
* Siehe dazu auch c't 22/12.
* <p>Die Klasse selbst muss nicht instantiiert werden, sondern alle Methoden
* sind static und beim Start der App wird automatisch {@link #rescanDevices()}
* aufgerufen, um die Liste der Devices zu erzeugen und somit eine echte
* SD-Karte zu finden. Die Liste wird nicht automatisch aktualisiert, aber
* eine App kann rescanDevices() aufrufen. Auch ist ein BroadcastReceiver für
* automatische Updates implementiert, muss aber von der App
* aufgerufen werden (siehe unten).
*
* <h2>Die Funktionsblöcke</h2>
* <ol>
* <li>In Version 1.2 der Library ist eine neue Zugriffsmethode eingebaut, die
* das Einstellen des Speicherorts per PreferenceScreen erleichtert. Dazu bindet
* man ein {@link DevicesListPreference} in seine XML-Datei ein, definiert
* dort über ein paar Parameter, welche Devices angezeigt werden sollen und
* bekommt dann per statischer Methode
* {@link DevicesListPreference#getDevice(Context, android.content.SharedPreferences, String)}
* das gespeicherte Device mitgeteilt. Im erweiterten {@link Device} gibt
* es nun Methoden, die analog zu den Environment- oder Context-Methoden
* Unterverzeichnisse erzeugen. Von den hier aufgeführten Methoden benötigt
* man dann nur die für den BroadcastReceiver.
*
* <li>Ob die bei vielen modernen Geräten (v.a. Tablets) vorhandene externe
* SD-Karte existiert, liefert {@link #isSecondaryExternalStorageAvailable()},
* ob sie entfernbar ist, {@link #isSecondaryExternalStorageRemovable()}.
*
* <li>Um auf die Karte zugreifen zu können, sind von den "External"-Methoden
* (zum Zugriff auf die interne SD-Karte) Äquivalente mit "Secondary" im
* Namen vorhanden. Entsprechend {@link Environment} sind das
* {@link #getSecondaryExternalStorageDirectory()},
* {@link #getSecondaryExternalStoragePublicDirectory(String)},
* {@link #getSecondaryExternalStorageState()}
* und entsprechend {@link Context}
* {@link #getSecondaryExternalCacheDir(Context)},
* {@link #getSecondaryExternalFilesDir(Context, String)}.
*
* <li>Um alles noch einen Schritt zu vereinfachen, gibt es den Methodensatz
* auf Anregung von Sven Wiegand nochmal mit "Card" im Namen. Sie greifen
* auf die externe SD-Karte zu und machen automatisch ein Fallback auf
* /mnt/sdcard, falls keine externe vorhanden ist. Wenn ein sekundärer Slot
* vorhanden ist, aber keine Karte eingesteckt, wird auch der Fallback
* benutzt. Ob die primäre oder sekundäre Speicherkarte benutzt wird, lässt
* sich per {@link #isSecondaryExternalStorageAvailable()} herausfinden.
* <p>
* Die App muss natürlich überprüfen, ob die Daten an der Speicherstelle
* noch vorhanden sind; das muss aber sowieso sein.
* <p>
* Etwas problematisch: Wenn die App gestartet wird, bevor eine sekundäre
* Speicherkarte eingelegt wird, greifen diese Methoden auf diese Karte zu
* und finden dort die Daten nicht, obwohl sie auf dem internen Speicher
* liegen. Als Abhilfe sollte die App sich den Speicherort evtl. abspeichern.
* <p>
* Implementiert sind: {@link #getCardDirectory()},
* {@link #getCardPublicDirectory(String)}, {@link #getCardState()} ,
* {@link #getCardCacheDir(Context)}, {@link #getCardFilesDir(Context, String)}.
*
* <li>Weil das Auffinden der SD-Karte versagen kann oder weil Anwender
* auswählen können sollen, wo sie ihre Daten speichern, ist auch eine
* Durchsuchfunktion für alle extern anbindbaren Geräte (diese Zweit-SD,
* aber auch USB-Geräte oder Kartenleser) vorhanden. Eine App kann
* damit eine Liste aller verfügbaren Speichermedien (inklusive der
* internen SD-Karte) anzeigen, zusammen mit deren Kapazität und
* freien Speicherplatz: {@link #getDevices(String, boolean, boolean)}.
* Die zurückgegebene Liste kann man dann ausgeben; in
* {@link Device} gibts Methoden für Pfad, Name und Größe der Devices.
*
* <li>Zwei Hilfsmethoden, die die beide ab API9 und 13 in Environment
* vorhandenen Funktionen zur Analyse des Geräts auch unter API8 nutzbar
* machen, und die einige Besonderheiten von unseren Testgeräten
* berücksichtigen: {@link #isExternalStorageEmulated()} und
* {@link #isExternalStorageRemovable()}.
*
* <li>Hilfsmethoden zum Zugriff auf /data, /mnt/sdcard und ggf. die
* externe Karte per {@link Device}-Interface: {@link #getPrimaryExternalStorage()},
* {@link #getSecondaryExternalStorage()} und {@link #getInternalStorage()}
* </ol>
*
* <h2>Aktualisierung der Geräteliste</h2>
* Falls eine App mitbekommen will, wenn Geräte oder die SD-Karte
* eingesteckt und entfernt werden, erstellt sie entweder einen eigenen
* BroadcastReceiver oder (einfacher) übergibt {@link #registerRescanBroadcastReceiver()}
* ein Runnable oder, falls man Zugriff auf den Intent haben möchte, einen
* BroadcastReceiver..
* <ul>
* <li>Der eigene Receiver sollte in onReceive() {@link #rescanDevices()} aufrufen.
* Die Methode {@link #getRescanIntentFilter()} erzeugt den richtigen
* IntentFilter für den Receiver.
* <li>Der in registerRescanBroadcastReceiver() automatisch erzeugte Receiver
* ruft erst rescanDevices() und danach den übergebenen Runnable auf (der
* auch null sein kann). registerReceiver wird auch automatisch aufgerufen,
* aber an {@link Context#unregisterReceiver()} (üblicherweise in onDestroy())
* muss man selbst denken.
* </ul>
* <h2>Background</h2>
* Diese Liste aller mountbaren Geräte (wozu diese Zweit-SDs zählt) lässt sich
* glücklicherweise bei allen bisher getesteten Geräten aus der Systemdatei
* /system/etc/vold.fstab auslesen, einer Konfigurationsdatei eines
* Linux-Dämons, der genau für das Einbinden dieser Geräte zuständig ist.
* Es mag Custom-ROMs geben, wo diese Methode nicht funktioniert.
* <p>
* Der MountPoint für die zweite SD-Karte stand bei allen bisher getesteten
* Geräten direkt an erster Stelle dieser Datei, bei einigen nach /mnt/sdcard
* an zweiter Stelle.
* <p>
* Andere Algorithmen zum Herausfinden des MountPoints sind noch nicht
* implementiert; das würde ich erst machen, wenn diese Methode hier bei
* einem Gerät versagt. Denkbar wäre z.B. einfach eine Tabelle mit bekannten
* MountPoints, die man der Reihe nach abklappert.
* <p>
* Varianten des SD-Pfads sind:
* <li>Asus Transformer /Removable/MicroSD
* <li>HTC Velocity LTE /mnt/sdcard/ext_sd
* <li>Huawei MediaPad /mnt/external
* <li>Intel Orange /mnt/sdcard2
* <li>LG Prada /mnt/sdcard/_ExternalSD
* <li>Motorola Razr /mnt/sdcard-ext
* <li>Motorola Razr i /mnt/external1 (zeigt es sogar korrekt an!)
* <li>Motorola Xoom /mnt/external1
* <li>Samsung Note /mnt/sdcard/external_sd (und Pocket und Mini 2)
* <li>Samsung Note II /storage/extSdCard
* <li>Samsung S3 /mnt/extSdCard
* <p>
* Einige Hersteller hängen die SD-Karte unter /mnt ein, andere in die
* interne Karte /mnt/sdcard (was dazu führt, dass einige Unterverzeichnisse
* von /mnt/sdcard größer sind als der gesamte interne Speicherbereich ;-),
* wieder andere in ein anderes Root-Verzeichnis.
*
* @author Jörg Wirtgen (jow@ct.de)

* @version 1.4
*
* @version 1.5 - auch für API7; die Zugriffe hier auf {@link Context}.getXXXDir
* finden nun über die Methoden von mPrimary statt, was ein {@link DeviceExternal}
* ist und bei Versionen vor API8 eine Emulation fährt ähnlich der in
* {@link DeviceDiv} sowieso vorhandenen.
*/


public class DiskEnvironment {
	private static final String LOG_TAG = DiskEnvironment.class.getSimpleName();
	private static final boolean DEBUG = true;
	
	private static ArrayList<DeviceDiv> mDeviceList = null;
	private static boolean mExternalEmulated = false;
	private static Device mInternal = null;
	protected static DeviceExternal mPrimary = null;
	private static DeviceDiv mSecondary = null;
	
	public final static String PATH_PREFIX = "/Android/data/";
	static {
		rescanDevices();
	}
	
	
	/**
	* Fragt ab, ob die Zweit-SD vorhanden ist. Der genauere Status kann
	* danach per {@link #getSecondaryExternalStorageState()} abgefragt werden.
	* @return true, wenn eine Zweit-SD vorhanden und eingelegt ist,
	* false wenn nicht eingelegt oder kein Slot vorhanden
	*/
	public static boolean isSecondaryExternalStorageAvailable() {
		Log.d(LOG_TAG, "isSecondaryExternalStorageAvailable() - mSecondary : "+mSecondary);
		if( mSecondary!=null) Log.d(LOG_TAG, "isSecondaryExternalStorageAvailable() - mSecondary : "+mSecondary.getName()+" isAvailable "+mSecondary.isAvailable() + " mount " +mSecondary.getMountPoint());
		Log.d(LOG_TAG, "isSecondaryExternalStorageAvailable() - getExternalStorageState "+ Environment.getExternalStorageState());
		return mSecondary!=null && mSecondary.isAvailable();
	}
	
	
	/**
	* Zeigt an, ob die Zweit-SD entfernt werden kann; derzeit kenne ich kein
	* Gerät, bei dem die fest eingebaut wäre, also immer true
	* @return true
	* @throws NoSecondaryStorageException falls keine Zwei-SD vorhanden
	* @see #isSecondaryExternalStorageAvailable()
	*/
	public final static boolean isSecondaryExternalStorageRemovable() throws NoSecondaryStorageException {
	if (mSecondary==null) throw new NoSecondaryStorageException();
	return true;
	}
	
	
	/**
	* Ein Zeiger auf die Zweit-SD, falls gefunden
	* @return das Verzeichnis der Zwei-SD
	* @throws NoSecondaryStorageException wenn keine Zwei-SD vorhanden oder nicht eingelegt
	* @see #isSecondaryExternalStorageAvailable()
	*/
	public static File getSecondaryExternalStorageDirectory() throws NoSecondaryStorageException {
	if (mSecondary==null) throw new NoSecondaryStorageException();
	return mSecondary.getFile();
	}
	
	
	/**
	* Liefert den Status einer zweiten SD-Karte oder wirt eine Exception
	* <p>
	* Zum Schreiben auf die Karte ist eine Permission notwendig:
	* <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	* <p>
	* TODO ab JellyBean 4.1 soll es auch eine Read-Permission geben?!
	* @return einer von den drei in Environment definierten States
	* MEDIA_MOUNTED, _MOUNTED_READ_ONLY und _REMOVED
	* @throws NoSecondaryStorageException wenn kein zweiter SD-Slot vorhanden
	*
	* @see #isSecondaryExternalStorageAvailable()
	*/
	public static String getSecondaryExternalStorageState() throws NoSecondaryStorageException {
		if (mSecondary==null) throw new NoSecondaryStorageException();
		return mSecondary.getState();
	}
	
	
	/**
	* Gibt die Public-Directories auf der Zweit-SD zurück; legt
	* sie (wie die Environment-Methode) nicht an.
	* @param s ein String aus Environment.DIRECTORY_xxx,
	* darf nicht null sein. (Funktioniert auch mit anderen Pfadnamen
	* und mit verschachtelten)
	* @return ein File dieses Verzeichnisses. Wenn Schreibzugriff gewährt,
	* wird es angelegt, falls nicht vorhanden
	* @throws NoSecondaryStorageException falls keine Zweit-SD vorhanden
	*/
	public static File getSecondaryExternalStoragePublicDirectory(String s) throws NoSecondaryStorageException {
	if (mSecondary==null) throw new NoSecondaryStorageException();
	if (s==null) throw new IllegalArgumentException("s darf nicht null sein");
	return mSecondary.getPublicDirectory(s);
	}
	
	
	/**
	* Replica of the context method getExternalFilesDir (String) with two differences:
	* <ol>
	* <li> You have to pass Context
	* <li> except for version >= KITKAT, The directory is not deleted when the app is uninstalled
	* </ Ol>
	* @param Context of the application
	* @param s ein String aus Environment.DIRECTORY_xxx, kann aber auch
	* ein anderer (verschachtelter) sein oder null
	* @return das Verzeichnis. Wird angelegt, wenn man Schreibzugriff hat
	* @throws NoSecondaryStorageException falls keine Zwei-SD vorhanden
	*/
	@SuppressLint("NewApi")
	public static File getSecondaryExternalFilesDir(Context context, String s) throws NoSecondaryStorageException {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			File[] files =context.getExternalFilesDirs(s);
			if(files.length<2) throw new NoSecondaryStorageException();
		} 
		if (mSecondary==null) throw new NoSecondaryStorageException();
		if (context==null) throw new IllegalArgumentException("context cannot be null");
		return mSecondary.getFilesDir(context, s);		
	}
	
	
	public static File getSecondaryExternalCacheDir(Context context) throws NoSecondaryStorageException {
	if (mSecondary==null) throw new NoSecondaryStorageException();
	if (context==null) throw new IllegalArgumentException("context cannot be null");
	return mSecondary.getCacheDir(context);
	}
	
	
	/*
	* Implementers are: {@link #getCardDirectory()},
	* {@link #getCardPublicDirectory(String)}, {@link #getCardStorageState()} ,
	* {@link #getCardCacheDir(Context)}, {@link #getCardFilesDir(Context, String)}.
	*/
	public static File getCardDirectory() {
	if (isSecondaryExternalStorageAvailable())
	try {return getSecondaryExternalStorageDirectory();}
	catch (NoSecondaryStorageException e) {throw new RuntimeException("NoSecondaryException received even if the storage is declared available"); }
	else
	return Environment.getExternalStorageDirectory();
	}
	
	public static File getCardPublicDirectory(String dir) {
	if (isSecondaryExternalStorageAvailable())
	try {return getSecondaryExternalStoragePublicDirectory(dir);}
	catch (NoSecondaryStorageException e) {throw new RuntimeException("NoSecondaryException received even if the storage is declared available"); }
	else
	return mPrimary.getPublicDirectory(dir);
	}
	
	public static String getCardState() {
	if (isSecondaryExternalStorageAvailable())
	try {return getSecondaryExternalStorageState();}
	catch (NoSecondaryStorageException e) {throw new RuntimeException("NoSecondaryException received even if the storage is declared available"); }
	else
	return Environment.getExternalStorageState();
	}
	
	public static File getCardCacheDir(Context ctx) {
	if (isSecondaryExternalStorageAvailable())
	try {return getSecondaryExternalCacheDir(ctx);}
	catch (NoSecondaryStorageException e) {throw new RuntimeException("NoSecondaryException received even if the storage is declared available"); }
	else
	return mPrimary.getCacheDir(ctx);
	}
	
	public static File getCardFilesDir(Context ctx, String dir) {
	if (isSecondaryExternalStorageAvailable())
	try {return getSecondaryExternalFilesDir(ctx, dir);}
	catch (NoSecondaryStorageException e) {throw new RuntimeException("NoSecondaryException received even if the storage is declared available"); }
	else
	return mPrimary.getFilesDir(ctx, dir);
	}
	
	
	/**
	* Alternative zu {@code Environment#isExternalStorageEmulated() },
	* die ab API8 funktioniert. Wenn true geliefert wird, handelt es sich
	* um ein Gerät mit "unified memory", bei dem /data und /mnt/sdcard
	* auf denselben Speicherbereich zeigen. App2SD ist dann deaktiviert,
	* und zum Berechnen des freien Speichers darf man nicht den der beiden
	* Partitionen addieren, sondern nur einmal zählen.
	*
	* @return true, falls /mnt/sdcard und /data auf den gleichen Speicherbereich zeigen;
	* false, falls /mnt/sdcard einen eigenen (nicht notwendigerweise auf einer
	* externen SD-Karte liegenden!) Speicherbereich beschreibt
	*
	* @see #isExternalStorageRemovable()
	*/
	public static boolean isExternalStorageEmulated() {
	return mExternalEmulated;
	}
	
	
	/**
	* Alternative zu {@link Environment#isExternalStorageRemovable()},
	* die ab API8 funktioniert. Achtung: Die Bedeutung ist eine subtil andere
	* als beim Original-Aufruf. Hier geht es (eher zu Hardware-Diagnosezwecken)
	* darum, ob /mnt/sdcard eine physische Karte ist, die der Nutzer
	* herausnehmen kann. Der Original-Aufruf liefert true, wenn es sein kann,
	* dass auf /mnt/sdcard nicht zugegriffen werden kann, was auch bei fest
	* eingebauten Karten der Fall sein kann, und zwar wenn sie per USB
	* an einen PC freigegeben werden können und währenddessen nicht
	* für Android im Zugriff stehen.
	*
	* @return true, falls /mnt/sdcard auf einer entnehmbaren
	* physischen Speicherkarte liegt
	* false, falls das ein fest verlöteter Speicher ist - das heißt nicht,
	* dass immer auf den Speicher zugegriffen werden kann, ein
	* Status-Check muss dennoch stattfinden (anders als
	* bei Environment.isExternalStorageRemovable())
	*
	* @see #isExternalStorageEmulated()
	*/
	public static boolean isExternalStorageRemovable() {
	return mPrimary.isRemovable();
	}
	
	
	
	/**
	* Hilfe zum Erstellen eines BroadcastReceivers: So muss der passende
	* IntentFilter aussehen, damit der Receiver alle Änderungen mitbekommt.
	* Wenn man einen eigenen Receiver programmiert statt
	* {@link Environment2#registerRescanBroadcastReceiver(Context, Runnable)}
	* zu nutzen, sollte man dort {@link Environment2#rescanDevices()} aufrufen.
	* @return einen IntentFilter, der auf alle Intents hört, die einen Hardware-
	* und Kartenwechsel anzeigen
	* @see IntentFilter
	*/
	public static IntentFilter getRescanIntentFilter() {
	if (mDeviceList==null) rescanDevices();
	IntentFilter filter = new IntentFilter();
	filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL); // rausgenommen
	filter.addAction(Intent.ACTION_MEDIA_MOUNTED); // wieder eingesetzt
	filter.addAction(Intent.ACTION_MEDIA_REMOVED); // entnommen
	filter.addAction(Intent.ACTION_MEDIA_SHARED); // per USB am PC
	// geht ohne folgendes nicht, obwohl das in der Doku nicht so recht steht
	filter.addDataScheme("file");
	
	/*
	* die folgenden waren zumindest bei den bisher mit USB getesteten
	* Geräten nicht notwendig, da diese auch bei USB-Sticks und externen
	* SD-Karten die ACTION_MEDIA-Intents abgefeuert haben
	filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
	filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
	*/
	return filter;
	}
	
	
	/**
	* BroadcastReceiver, der einen Rescan durchführt und (als Callback) das
	* übergebene Runnable aufruft. Muss mit unregisterReceiver freigegeben
	* werden; dafür ist der Aufrufer verantwortlich. Das Registrieren des
	* Receivers wird hier schon durchgeführt (mit getRescanIntentFilter)
	* <p>
	* Das geht dann (z.B. in onCreate() ) so (mit {@code BroadcastReceiver mRescanReceiver;}) : <pre>
	mRescanReceiver = Environment2.registerRescanBroadcastReceiver(this, new Runnable() {
	public void run() {
	auszuführende Befehle
	}
	});</pre>
	* <p>
	* und später (z.B. in onDestroy() ): {@code unregisterReceiver(mRescanReceiver);}
	* <p>
	* Der hier implementierte Receiver macht nichts anderes als {@link #rescanDevices() }
	* und dann den Runnable aufzurufen.
	* <p>
	* TODO Problematisch ist, dass bei MEDIA_BAD_REMOVAL die Daten des fälschlich
	* entnommenen Sticks noch vorhanden sind.
	*
	* @param context der Context, in dem registerReceiver aufgerufen wird
	* @param r der Runnable, der bei jedem An- und Abmelden von Devices
	* ausgeführt wird; kann auch null sein
	* @return der BroadcastReceiver, der später unregisterReceiver übergeben
	* werden muss. Registriert werden muss er nicht, das führt die
	* Methode hier durch.
	* @see #getRescanIntentFilter()
	* @see BroadcastReceiver
	*/
	public static BroadcastReceiver registerRescanBroadcastReceiver(Context context, final Runnable r) {
	if (mDeviceList==null) rescanDevices();
	BroadcastReceiver br = new BroadcastReceiver() {
	@Override public void onReceive(Context context, Intent intent) {
	if (DEBUG) Log.i(LOG_TAG, "Storage: "+intent.getAction()+"-"+intent.getData());
	updateDevices();
	if (r!=null) r.run();
	}
	};
	context.registerReceiver(br, getRescanIntentFilter());
	return br;
	}
	
	
	/**
	* Wie {@link #registerRescanBroadcastReceiver(Context, Runnable)}, nur dass ein BroadcastReceiver
	* übergeben werden muss, deren onReceive dann aufgerufen wird. Der Unterschied: Hier bekommt
	* der Aufrufer den Intent mitgeteilt, beim anderen Aufruf nicht.
	*
	* @param context der Context der App
	* @param r der BroadcastReceiver, dessen onReceive() aufgerufen werden soll
	* @return ein BroadcastReceiver (nicht der übergebene), der später dann unregisterReceiver
	* übergeben werden muss. Registriert werden muss er nicht, das führt die Methode durch.
	* @since 1.4
	*/
	public static BroadcastReceiver registerRescanBroadcastReceiver(Context context, final BroadcastReceiver r) {
	if (mDeviceList==null) rescanDevices();
	BroadcastReceiver br = new BroadcastReceiver() {
	@Override public void onReceive(Context context, Intent intent) {
	if (DEBUG) Log.i(LOG_TAG, "Storage: "+intent.getAction()+"-"+intent.getData());
	updateDevices();
	if (r!=null) r.onReceive(context, intent);
	}
	};
	context.registerReceiver(br, getRescanIntentFilter());
	return br;
	}
	
	
	/**
	* Scannt die Verfügbarkeit aller Geräte neu, ohne {@link #rescanDevices()} aufzurufen.
	*
	* <p>Wird vom BroadcastReceiver aufgerufen. Falls die App einen eigenen
	* BroadcastReceiver zum Erkennen von Wechseln bei Devices schreibt,
	* sollte in dessen onReceive() diese Methode aufgerufen werden.
	*
	* @see Environment2#registerRescanBroadcastReceiver(Context, Runnable)
	* @since 1.3
	*/
	public static void updateDevices() {
	for (Device i : mDeviceList) {i.updateState();}
	mPrimary.updateState();
	}
	
	
	/**
	* Sucht das Gerät nach internen und externen Speicherkarten und USB-Geräten
	* ab. Wird automatisch beim App-Start aufgerufen (in einem static-initializer) und
	* muss nach bisherigen Erkenntnissen nie von der App aufgerufen werden.
	*/
	@SuppressLint("NewApi")
	public static void rescanDevices() {
		
		
		mDeviceList = new ArrayList<DeviceDiv>(10);
		mPrimary = new DeviceExternal();
		
		// vold.fstab lesen; TODO bei Misserfolg eine andere Methode
		if (!scanVold("vold.fstab")) scanVold("vold.conf");
		
	    // zeigen /mnt/sdcard und /data auf denselben Speicher?
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    	 mExternalEmulated = Environment.isExternalStorageEmulated();
	    } else {
		     // vor Honeycom gab es den unified memory noch nicht
		     mExternalEmulated = false;
	    }
		
	    // search path to the second SD card; been implemented only method 1
	    // Method 1: just the first entry in vold.fstab, possibly a mnt / / sdcard double-adjusted
	    // Method 2: the first with "sd", if not available the first with "ext"
	    // Method 3: the first available
		if (mDeviceList.size()==0) {
			mSecondary = null;
			// TODO Geräte mit interner SD und Android 2 wie Nexus S
			// if (nexus) mPrimary.setRemovable(false);
		} else {
			mSecondary = mDeviceList.get(0);
			if (mSecondary.getName().contains("usb")) {
				// z.B. HTC One X+
				mSecondary = null;
			} else {
				// jau, SD gefunden
				mSecondary.setName("SD-Card");
				// Hack
				if (mPrimary.isRemovable()) Log.w(LOG_TAG, "isExternStorageRemovable overwrite (secondary sd found) auf false");
				mPrimary.setRemovable(false);
			}
		}
		
		// if not valid, try the alternat method to find the mount point
		if(mSecondary != null && !mSecondary.isAvailable()){
			// check with alternate method
			Log.d(LOG_TAG, "rescanDevices() - mSecondary not available trying alternate method");
			
			
			
			List<StorageVolume> listStorages = StorageHelper.getStorages(false);
			for(StorageVolume storage : listStorages){
				Log.d(LOG_TAG, "rescanDevices() - StorageVolume "+storage.toString());
				if(storage.isRemovable() && !storage.isEmulated()){
				    try {
					SimpleStringSplitter sp = new SimpleStringSplitter(' ');
				     sp.setString(storage.fileSystem+" "+storage.file.getCanonicalPath());
						
					
						 Log.d(LOG_TAG, "rescanDevices() - trying new storage "+storage.fileSystem+" "+storage.file.getCanonicalPath()+" "+storage.device);
						 
						 DeviceDiv d = new DeviceDiv(sp);
						 mSecondary = d;
						 
						 
						 
				    } catch (IOException e) {
						Log.e(LOG_TAG, e.getMessage(), e);
					}
				}
			}
		}
		
	}
	
	
	/**
	* The vold configuration file to evaluate, which usually
	* lies in /system/etc/ .
	* @ Param name a string with the file name (or vold.fstab vold.conf)
	* @ Return true if worked; false if file does not (fully) Could be 
	* * read. If false, the read so far
	* Devices not deleted but remain in the list
	* Included. But so far no device I still come across,
	* In which this trick did not work.
	*/
	private static boolean scanVold(String name) {
	String s, f;
	boolean prefixScan = true; // sdcard-Prefixes
	SimpleStringSplitter sp = new SimpleStringSplitter(' ');
	     try {
	     BufferedReader buf = new BufferedReader(new FileReader(Environment.getRootDirectory().getAbsolutePath()+"/etc/"+name), 2048);
	     s = buf.readLine();
	     while (s!=null) {
	     sp.setString(s.trim());
	     f = sp.next(); // dev_mount oder anderes
	         if ("dev_mount".equals(f)) {
	         DeviceDiv d = new DeviceDiv(sp);
	        
	         if (TextUtils.equals(mPrimary.getMountPoint(), d.getMountPoint())) {
	         // ein wenig Spezialkrams über /mnt/sdcard herausfinden
	        
	         // wenn die Gingerbread-Funktion isExternalStorageRemovable nicht da ist, diesen Hinweis nutzen
	         if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD)
	         mPrimary.setRemovable(true);
	         // Then the default entry is removable
	         // Actually this is not enough here, because the vold entries for the primary SD card are much more complex,
	         // Is often because of what non-removable. But auszuwerden all these proprietary clothes
	         // Would be far too complex. A workable compromise seems to be just from 2.3 to
	         // To leave isExternalStorageRemovable what is already set up in Device (). In the
	         // So far emerged devices with 2.2 again, the reference in vold seems to klappen.vccfg

	         // For example Galaxy Note hangs "encryptable_nonremovable" to
	         while (sp.hasNext()) {
	         f = sp.next();
	         if (f.contains("nonremovable")) {
	         mPrimary.setRemovable(false);
	         Log.w(LOG_TAG, "isExternStorageRemovable overwrite ('nonremovable') auf false");
	         }
	         }
	         prefixScan = false;
	         } else
	         // nur in Liste aufnehmen, falls nicht Dupe von /mnt/sdcard
	         mDeviceList.add(d);
	        
	         } else if (prefixScan) {
	         // Weitere Untersuchungen nur, wenn noch vor sdcard-Eintrag
	         // etwas unsauber, da es eigentlich in {} vorkommen muss, was ich hier nicht überprüfe
	        
	         if ("discard".equals(f)) {
	         // manche (Galaxy Note) schreiben "discard=disable" vor den sdcard-Eintrag.
	         sp.next(); // "="
	         f = sp.next();
	         if ("disable".equals(f)) {
	         mPrimary.setRemovable(false);
	         Log.w(LOG_TAG, "isExternStorageRemovable overwrite ('discard=disable') auf false");
	         } else if ("enable".equals(f)) {
	         // ha, denkste... bisher habe ich den Eintrag nur bei zwei Handys gefunden, (Galaxy Note, Galaxy Mini 2), und
	         // da stimmte er *nicht*, sondern die Karten waren nicht herausnehmbar.
	         // mPrimary.mRemovable = true;
	         Log.w(LOG_TAG, "isExternStorageRemovable overwrite overwrite ('discard=enable'), bleibt auf "+mPrimary.isRemovable());
	         } else
	         Log.w(LOG_TAG, "disable-Eintrag unverständlich: "+f);
	         }
	        
	         }
	     s = buf.readLine();
	     }
	     buf.close();
	     Log.v(LOG_TAG, name+" gelesen; Geräte gefunden: "+mDeviceList.size());
	     return true;
	     } catch (Exception e) {
	     Log.e(LOG_TAG, "kann "+name+" nicht lesen: "+e.getMessage());
	     return false;
	     }
	}
	
	
	/**
	* Compile a list of all found Removable devices. The list can
	* Be limited by device name and other parameters.
	*
	* @param key ein String zum Einschränken der Liste. Findet nur die Devices
	* mit dem String in getName() oder alle, falls null.
	* @param available ein Boolean zum Beschränken der Liste auf vorhandene
	* (eingesteckte) Geräte. false findet alle, true nur diejenigen, die eingesteckt sind.
	* Wenn false, können Device-Einträge zurückgegeben werden, deren
	* getSize()-Objekt null ist.
	* @param intern ein Boolean, der bestimmt, ob der interne Speicher (/mnt/sdcard)
	* mit in die Liste übernommen wird (unter Berücksichtigung von available,
	* aber nicht key).
	* @param data ein Boolean, der bestimmt, ob der data-Speicher (/data) mit
	* in die Liste übernommen wird
	* @return ein Array mit allen {@link Device}, die den Suchkriterien entsprechen
	*/
	public static Device[] getDevices(String key, boolean available, boolean intern, boolean data) {
	if (key!=null) key = key.toLowerCase();
	ArrayList<Device> temp = new ArrayList<Device>(mDeviceList.size()+2);
	if (data) temp.add(getInternalStorage());
	if (intern && ( !available || mPrimary.isAvailable())) temp.add(mPrimary);
	for (Device d : mDeviceList) {
	if ( ((key==null) || d.getName().toLowerCase().contains(key)) && (!available || d.isAvailable()) ) temp.add(d);
	}
	return temp.toArray(new Device[temp.size()]);
	}
	
	
	public static Device getPrimaryExternalStorage() {
	return mPrimary;
	}
	
	
	public static Device getSecondaryExternalStorage() throws NoSecondaryStorageException {
	if (mSecondary==null) throw new NoSecondaryStorageException();
	return mSecondary;
	}
	
	
	public static Device getInternalStorage() {
	if (mInternal==null) mInternal = new DeviceIntern();
	return mInternal;
	}
}
