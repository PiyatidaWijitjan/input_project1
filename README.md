# Shopee Clone App (ตัวอย่างโปรเจกต์ Android Studio)

โปรเจกต์นี้เป็นตัวอย่างแอปขายของออนไลน์คล้าย Shopee เขียนด้วย **Kotlin + View Binding**
เน้นการทำหน้าจ่ายเงินที่มี **Cascading Dropdown / Smart Address Lookup**
(จังหวัด → อำเภอ → ตำบล → รหัสไปรษณีย์ ที่ input ขึ้นต่อกัน)

## วิธีเปิดโปรเจกต์

1. เปิด Android Studio (แนะนำเวอร์ชัน Koala ขึ้นไป, AGP 8.4+)
2. เลือก **Open** แล้วชี้ไปที่โฟลเดอร์ `ShopeeCloneApp`
3. รอ Gradle sync ให้เสร็จ (ต้องมีอินเทอร์เน็ตครั้งแรกเพื่อดาวน์โหลด dependencies)
4. กด Run ▶️ บน emulator หรืออุปกรณ์จริง (minSdk 24)

## โครงสร้างโปรเจกต์

```
app/src/main/
├── AndroidManifest.xml
├── assets/
│   └── thai_address.json        # ข้อมูลจังหวัด/อำเภอ/ตำบล/รหัสไปรษณีย์ (ตัวอย่าง)
├── java/com/example/shopeeclone/
│   ├── MainActivity.kt          # หน้าหลัก + BottomNavigationView
│   ├── model/
│   │   ├── Product.kt
│   │   └── Address.kt           # Province / District / SubDistrict
│   ├── repository/
│   │   ├── ProductRepository.kt
│   │   └── AddressRepository.kt # อ่านไฟล์ JSON แปลงเป็น List<Province>
│   ├── adapter/
│   │   └── ProductAdapter.kt    # RecyclerView grid adapter
│   └── ui/
│       ├── home/HomeFragment.kt         # หน้าหลัก: grid สินค้า
│       ├── profile/ProfileFragment.kt   # หน้าโปรไฟล์ (placeholder)
│       ├── settings/SettingsFragment.kt # หน้าตั้งค่า (placeholder)
│       ├── detail/ProductDetailActivity.kt   # หน้ารายละเอียดสินค้า
│       └── checkout/CheckoutActivity.kt      # หน้าจ่ายเงิน (cascading dropdown)
└── res/
    ├── layout/                  # XML layout ทั้งหมด
    ├── menu/bottom_nav_menu.xml
    └── values/                  # colors, strings, themes
```

## หัวใจของ Cascading Dropdown (input dependency)

อยู่ที่ `CheckoutActivity.kt`:

1. **จังหวัด (Province)** — โหลดรายชื่อทั้งหมดจาก `AddressRepository` ใส่ใน
   `AutoCompleteTextView` (Material Exposed Dropdown Menu) ทันทีที่เปิดหน้าจอ
2. เมื่อผู้ใช้ **เลือกจังหวัด** → ระบบจะ:
   - ล้างค่าอำเภอ/ตำบล/รหัสไปรษณีย์เดิมทิ้ง
   - เปิดใช้งาน (enable) dropdown อำเภอ และเติมเฉพาะอำเภอของจังหวัดนั้น ๆ
3. เมื่อผู้ใช้ **เลือกอำเภอ** → ระบบจะ:
   - ล้างค่าตำบล/รหัสไปรษณีย์เดิมทิ้ง
   - เปิดใช้งาน dropdown ตำบล และเติมเฉพาะตำบลของอำเภอนั้น ๆ
4. เมื่อผู้ใช้ **เลือกตำบล** → ระบบจะเติม **รหัสไปรษณีย์อัตโนมัติ** (Smart Lookup)
   ลงในช่องรหัสไปรษณีย์ที่เป็น read-only ให้ทันที

ก่อนจังหวัด/อำเภอ/ตำบลระดับบนถูกเลือก ช่อง input ระดับล่างจะถูก **disable ไว้ก่อน**
(`tilDistrict.isEnabled = false`, `tilSubDistrict.isEnabled = false`) เพื่อบังคับลำดับการกรอก
และป้องกันข้อมูลไม่สอดคล้องกัน (เช่น เลือกจังหวัดหนึ่งแต่ตำบลของอีกจังหวัด)

## การขยายผล (สำหรับใช้งานจริง)

- ข้อมูลใน `assets/thai_address.json` เป็นชุดตัวอย่างเพียงไม่กี่จังหวัด
  หากต้องการข้อมูลครบ 77 จังหวัดทั่วประเทศไทย สามารถแทนที่ไฟล์นี้ด้วยชุดข้อมูลที่สมบูรณ์กว่า
  (เช่น ชุดข้อมูล thai-province-data ที่มีให้ดาวน์โหลดแบบเปิดเผยหลายที่) โดยยังคงโครงสร้าง
  `province -> districts -> subdistricts (name, zipcode)` เดิมไว้ได้เลย ไม่ต้องแก้โค้ด Kotlin
- หากต้องการให้เร็วขึ้นกับข้อมูลจำนวนมาก แนะนำเปลี่ยนจาก `ArrayAdapter` ธรรมดา
  เป็นการโหลดแบบ Room database หรือแคชผลลัพธ์ไว้ใน ViewModel
- รูปสินค้าปัจจุบันใช้ drawable placeholder เดียวกันทุกชิ้น สามารถเปลี่ยนเป็นโหลดภาพจริง
  ผ่านไลบรารีเช่น Glide หรือ Coil ได้ทันที
