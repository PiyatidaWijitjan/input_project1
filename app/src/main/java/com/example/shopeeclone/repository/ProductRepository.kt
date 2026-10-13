package com.example.shopeeclone.repository

import com.example.shopeeclone.R
import com.example.shopeeclone.model.Product

object ProductRepository {

    fun getSampleProducts(): List<Product> = listOf(
        Product(1, "เสื้อยืดโอเวอร์ไซส์", 199.0, "เสื้อยืดผ้าคอตตอน 100% ใส่สบาย ทรงโอเวอร์ไซส์ มีหลายสี", R.drawable.placeholder_product),
        Product(2, "หูฟังบลูทูธไร้สาย", 590.0, "หูฟังไร้สายเสียงดี ตัดเสียงรบกวน แบตอึด 20 ชั่วโมง", R.drawable.placeholder_product),
        Product(3, "กระเป๋าสะพายข้าง", 350.0, "กระเป๋าหนัง PU กันน้ำ ดีไซน์เรียบหรู ใช้ได้ทุกโอกาส", R.drawable.placeholder_product),
        Product(4, "รองเท้าผ้าใบ", 890.0, "รองเท้าผ้าใบสำหรับวิ่งและใส่ลำลอง พื้นนุ่มรองรับแรงกระแทก", R.drawable.placeholder_product),
        Product(5, "สมาร์ทวอทช์", 1290.0, "นาฬิกาอัจฉริยะวัดชีพจร นับก้าว กันน้ำ IP68", R.drawable.placeholder_product),
        Product(6, "แว่นกันแดด", 259.0, "แว่นกันแดดเลนส์ป้องกันรังสี UV400 ทรงคลาสสิก", R.drawable.placeholder_product),
        Product(7, "พาวเวอร์แบงค์ 20000mAh", 450.0, "พาวเวอร์แบงค์ชาร์จเร็ว รองรับ Fast Charge 2 พอร์ต", R.drawable.placeholder_product),
        Product(8, "หมวกแก๊ป", 149.0, "หมวกแก๊ปผ้าฝ้าย ปรับขนาดได้ ใส่กันแดดกันฝุ่น", R.drawable.placeholder_product)
    )
}
