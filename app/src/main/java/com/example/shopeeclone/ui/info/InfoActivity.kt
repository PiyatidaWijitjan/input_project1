package com.example.shopeeclone.ui.info

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shopeeclone.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TYPE = "extra_type"
        const val TYPE_HELP = "help"
        const val TYPE_PRIVACY = "privacy"
        const val TYPE_TERMS = "terms"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val (title, body) = when (intent.getStringExtra(EXTRA_TYPE)) {
            TYPE_PRIVACY -> "นโยบายความเป็นส่วนตัว" to PRIVACY_TEXT
            TYPE_TERMS -> "ข้อกำหนดการใช้งาน" to TERMS_TEXT
            else -> "ศูนย์ช่วยเหลือ" to HELP_TEXT
        }
        binding.toolbar.title = title
        binding.tvBody.text = body
    }
}

private val HELP_TEXT = """
คำถามที่พบบ่อย

ถาม: สั่งซื้อสินค้าอย่างไร?
ตอบ: เลือกสินค้าที่หน้าหลัก กดปุ่มซื้อสินค้า กรอกที่อยู่จัดส่ง เลือกวิธีจัดส่งและชำระเงิน แล้วกดสั่งซื้อสินค้า

ถาม: ใช้คูปองและเหรียญอย่างไร?
ตอบ: ที่หน้าชำระเงินเลือกคูปองจากรายการ และเปิดสวิตช์ใช้เหรียญเพื่อหักเป็นส่วนลด (1 เหรียญ = ฿1)

ถาม: ยกเลิกคำสั่งซื้อได้เมื่อไหร่?
ตอบ: ยกเลิกได้ก่อนสินค้าเริ่มจัดส่ง ที่เมนูโปรไฟล์ > ประวัติการสั่งซื้อ

ถาม: ลืมรหัสผ่านทำอย่างไร?
ตอบ: กด "ลืมรหัสผ่าน?" ที่หน้าเข้าสู่ระบบ แล้วยืนยันด้วยเบอร์โทรและอีเมลที่ลงทะเบียนไว้

ต้องการความช่วยเหลือเพิ่มเติม ติดต่อเราได้ที่เมนูโปรไฟล์ > ติดต่อเรา
""".trimIndent()

private val PRIVACY_TEXT = """
นโยบายความเป็นส่วนตัว

1. ข้อมูลที่เราเก็บ
ชื่อ เบอร์โทรศัพท์ อีเมล ที่อยู่จัดส่ง และประวัติการสั่งซื้อ เพื่อใช้ในการให้บริการ

2. การใช้ข้อมูล
ใช้เพื่อยืนยันตัวตน จัดส่งสินค้า และปรับปรุงบริการ เราไม่ขายข้อมูลส่วนบุคคลของท่านให้บุคคลภายนอก

3. การเก็บรักษา
ข้อมูลของท่านถูกเก็บไว้บนอุปกรณ์นี้ ท่านสามารถแก้ไขหรือลบข้อมูลได้ที่เมนูโปรไฟล์และตั้งค่า

4. สิทธิของท่าน
ท่านมีสิทธิขอเข้าถึง แก้ไข หรือลบข้อมูลส่วนบุคคลของท่านได้ตลอดเวลา

(ข้อความตัวอย่าง กรุณาแทนที่ด้วยนโยบายจริงของธุรกิจท่าน)
""".trimIndent()

private val TERMS_TEXT = """
ข้อกำหนดการใช้งาน

1. การสมัครสมาชิก
ผู้ใช้ต้องให้ข้อมูลที่ถูกต้องและรับผิดชอบการรักษารหัสผ่านของตนเอง

2. การสั่งซื้อ
คำสั่งซื้อจะสมบูรณ์เมื่อมีการยืนยันและชำระเงินตามวิธีที่เลือก ราคาและโปรโมชันอาจเปลี่ยนแปลงได้โดยไม่แจ้งล่วงหน้า

3. คูปองและเหรียญ
คูปองใช้ได้ครั้งเดียวตามเงื่อนไขที่ระบุ เหรียญไม่สามารถแลกเป็นเงินสดได้

4. การยกเลิก
สามารถยกเลิกคำสั่งซื้อได้ก่อนสินค้าเริ่มจัดส่ง

(ข้อความตัวอย่าง กรุณาแทนที่ด้วยข้อกำหนดจริงของธุรกิจท่าน)
""".trimIndent()