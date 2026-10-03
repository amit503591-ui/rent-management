package com.example.util

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    EN("en", "English", "🇬🇧"),
    HI("hi", "हिन्दी", "🇮🇳")
}

object AppStrings {
    fun get(lang: AppLanguage): Strings = if (lang == AppLanguage.HI) HindiStrings else EnglishStrings
}

interface Strings {
    val appTitle: String
    val languageName: String
    val switchToOtherLang: String
    val landlord: String
    val tenant: String
    val landlordPortal: String
    val tenantPortal: String
    val landlordLoginDesc: String
    val tenantLoginDesc: String
    val enterPasscode: String
    val loginButton: String
    val selectRoom: String
    val enterRoomPin: String
    val switchRole: String

    // Dashboard
    val dashboardTitle: String
    val collected: String
    val pendingDues: String
    val activeRooms: String
    val incomeTrends: String
    val incomeTrendsDesc: String
    val manageRoomsAndTenants: String
    val manageRoomsDesc: String
    val broadcastReminders: String
    val broadcastRemindersDesc: String
    val pendingBillsTab: String
    val paymentHistoryTab: String
    val recordPayment: String
    val generateBill: String
    val sendWhatsApp: String

    // Create Bill & Meter OCR
    val generateBillTitle: String
    val autoFetchMeterTitle: String
    val autoFetchMeterDesc: String
    val cameraBtn: String
    val photosBtn: String
    val startReading: String
    val endReading: String
    val unitsConsumed: String
    val electricityFee: String
    val baseRent: String
    val otherCharges: String
    val totalAmountDue: String
    val confirmSaveBill: String
    val scanningMeter: String

    // Tenant List & Management
    val tenantDirectoryTitle: String
    val addRoomBtn: String
    val searchPlaceholder: String
    val allFloors: String
    val deleteTenant: String
    val deleteConfirmTitle: String
    val deleteConfirmDesc: String
    val cancel: String
    val deletePermanently: String
    val editTenant: String
    val saveChanges: String

    // Tenant Portal
    val welcomeTenant: String
    val currentDueBalance: String
    val payViaUpi: String
    val billsAndReadings: String
    val receipts: String
    val reportMaintenance: String
}

object EnglishStrings : Strings {
    override val appTitle = "RentPulse"
    override val languageName = "English"
    override val switchToOtherLang = "🇮🇳 हिन्दी"
    override val landlord = "Landlord"
    override val tenant = "Tenant"
    override val landlordPortal = "Landlord Portal"
    override val tenantPortal = "Tenant Portal"
    override val landlordLoginDesc = "Manage rooms, meter readings & rent collection"
    override val tenantLoginDesc = "View live electricity usage, dues & pay rent"
    override val enterPasscode = "Enter Landlord PIN (Default: 9413)"
    override val loginButton = "Login as Landlord"
    override val selectRoom = "Select Your Room"
    override val enterRoomPin = "Enter Room 4-Digit PIN"
    override val switchRole = "Switch Role"

    override val dashboardTitle = "RentPulse Manager"
    override val collected = "Collected"
    override val pendingDues = "Pending Dues"
    override val activeRooms = "Rooms"
    override val incomeTrends = "Income Trends & Analytics"
    override val incomeTrendsDesc = "Monthly bar chart & room collection status"
    override val manageRoomsAndTenants = "Manage Floor Rooms & Tenants"
    override val manageRoomsDesc = "Add, edit, delete rooms & meter readings"
    override val broadcastReminders = "Broadcast WhatsApp Reminders"
    override val broadcastRemindersDesc = "Send one-tap payment notices to all pending rooms"
    override val pendingBillsTab = "Pending Bills"
    override val paymentHistoryTab = "Payment History"
    override val recordPayment = "Record Payment"
    override val generateBill = "Generate Bill"
    override val sendWhatsApp = "Send WhatsApp Notice"

    override val generateBillTitle = "Generate Bill & Meter OCR"
    override val autoFetchMeterTitle = "Auto-Fetch Reading from Camera"
    override val autoFetchMeterDesc = "Snap sub-meter dial or upload from photos to automatically extract exact reading (e.g. 1012.2)."
    override val cameraBtn = "Camera 📷"
    override val photosBtn = "Photos 🖼️"
    override val startReading = "Last Reading"
    override val endReading = "New Reading"
    override val unitsConsumed = "Units Consumed"
    override val electricityFee = "Electricity Fee (₹11/unit)"
    override val baseRent = "Base Room Rent"
    override val otherCharges = "Other Charges"
    override val totalAmountDue = "Total Amount Due"
    override val confirmSaveBill = "Confirm & Save Bill"
    override val scanningMeter = "Scanning electricity meter with AI OCR..."

    override val tenantDirectoryTitle = "Rooms & Tenants"
    override val addRoomBtn = "Add Room & Tenant"
    override val searchPlaceholder = "Search room number, tenant name..."
    override val allFloors = "All Floors"
    override val deleteTenant = "Delete Room & Tenant"
    override val deleteConfirmTitle = "Delete Room & Tenant?"
    override val deleteConfirmDesc = "This will permanently remove this tenant, bills and history."
    override val cancel = "Cancel"
    override val deletePermanently = "Delete Permanently"
    override val editTenant = "Edit Tenant Info"
    override val saveChanges = "Save Changes"

    override val welcomeTenant = "Welcome,"
    override val currentDueBalance = "Total Amount Due"
    override val payViaUpi = "Pay Instantly via UPI"
    override val billsAndReadings = "Bills & Meter Readings"
    override val receipts = "Payment Receipts"
    override val reportMaintenance = "Report Issue / Maintenance"
}

object HindiStrings : Strings {
    override val appTitle = "रेंटपल्स (RentPulse)"
    override val languageName = "हिन्दी"
    override val switchToOtherLang = "🇬🇧 English"
    override val landlord = "मकान मालिक"
    override val tenant = "किरायेदार"
    override val landlordPortal = "मकान मालिक पोर्टल"
    override val tenantPortal = "किरायेदार पोर्टल"
    override val landlordLoginDesc = "कमरे, बिजली मीटर रीडिंग और किराया वसूली प्रबंधित करें"
    override val tenantLoginDesc = "अपनी बिजली खपत, बकाया बिल देखें और ऑनलाइन किराया भरें"
    override val enterPasscode = "मकान मालिक पिन दर्ज करें (डिफ़ॉल्ट: 9413)"
    override val loginButton = "लॉगिन करें"
    override val selectRoom = "अपना कमरा चुनें"
    override val enterRoomPin = "कमरे का 4-अंकीय पिन दर्ज करें"
    override val switchRole = "भूमिका बदलें"

    override val dashboardTitle = "रेंटपल्स डैशबोर्ड"
    override val collected = "प्राप्त राशि"
    override val pendingDues = "कुल बकाया"
    override val activeRooms = "सक्रिय कमरे"
    override val incomeTrends = "मासिक आय एवं विश्लेषण"
    override val incomeTrendsDesc = "मासिक चार्ट और किराया भुगतान स्थिति देखें"
    override val manageRoomsAndTenants = "कमरे और किरायेदार प्रबंधन"
    override val manageRoomsDesc = "कमरा जोड़ें, बदलें, हटाएं और मीटर रीडिंग देखें"
    override val broadcastReminders = "व्हाट्सएप पर तगादा / बिल भेजें"
    override val broadcastRemindersDesc = "सभी बकाया कमरों को एक साथ व्हाट्सएप पर बिल भेजें"
    override val pendingBillsTab = "बकाया बिल"
    override val paymentHistoryTab = "भुगतान रसीदें"
    override val recordPayment = "भुगतान दर्ज करें"
    override val generateBill = "नया बिल बनाएं"
    override val sendWhatsApp = "व्हाट्सएप पर भेजें"

    override val generateBillTitle = "बिल बनाएं एवं मीटर स्कैन"
    override val autoFetchMeterTitle = "कैमरे से मीटर रीडिंग स्वतः लें"
    override val autoFetchMeterDesc = "सब-मीटर डायल की फोटो खींचे या गैलरी से चुनें, रीडिंग (जैसे 1012.2) अपने आप दर्ज हो जाएगी।"
    override val cameraBtn = "कैमरा 📷"
    override val photosBtn = "गैलरी 🖼️"
    override val startReading = "पिछली रीडिंग"
    override val endReading = "नई रीडिंग (वर्तमान)"
    override val unitsConsumed = "खपत यूनिट्स"
    override val electricityFee = "बिजली शुल्क (₹11/यूनिट)"
    override val baseRent = "कमरे का मूल किराया"
    override val otherCharges = "अन्य शुल्क"
    override val totalAmountDue = "कुल देय राशि"
    override val confirmSaveBill = "बिल सुरक्षित करें और जारी करें"
    override val scanningMeter = "मीटर रीडिंग स्कैन की जा रही है..."

    override val tenantDirectoryTitle = "कमरे और किरायेदार सूची"
    override val addRoomBtn = "नया कमरा और किरायेदार जोड़ें"
    override val searchPlaceholder = "कमरा नंबर या नाम खोजें..."
    override val allFloors = "सभी मंजिलें"
    override val deleteTenant = "कमरा और किरायेदार हटाएं"
    override val deleteConfirmTitle = "क्या आप कमरा हटाना चाहते हैं?"
    override val deleteConfirmDesc = "इस कमरे और किरायेदार के सभी पिछले बिल और रसीदें सुरक्षित रूप से हटा दी जाएंगी।"
    override val cancel = "रद्द करें"
    override val deletePermanently = "हमेशा के लिए हटाएं"
    override val editTenant = "किरायेदार जानकारी बदलें"
    override val saveChanges = "बदलाव सुरक्षित करें"

    override val welcomeTenant = "स्वागत है,"
    override val currentDueBalance = "कुल बकाया राशि"
    override val payViaUpi = "UPI (Paytm/GPay/PhonePe) से भुगतान करें"
    override val billsAndReadings = "बिल और मीटर रीडिंग"
    override val receipts = "भुगतान रसीदें"
    override val reportMaintenance = "समस्या / रिपेयर की शिकायत करें"
}
