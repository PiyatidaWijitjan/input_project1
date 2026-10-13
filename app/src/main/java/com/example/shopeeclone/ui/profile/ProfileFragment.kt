package com.example.shopeeclone.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shopeeclone.databinding.FragmentProfileBinding
import com.example.shopeeclone.databinding.ItemProfileMenuRowBinding
import com.example.shopeeclone.repository.AddressBookRepository
import com.example.shopeeclone.repository.RewardRepository
import com.example.shopeeclone.repository.UserRepository
import com.example.shopeeclone.repository.WishlistRepository
import com.example.shopeeclone.ui.address.AddressBookActivity
import com.example.shopeeclone.ui.orders.OrderHistoryActivity
import com.example.shopeeclone.ui.rewards.RewardsActivity
import com.example.shopeeclone.ui.wishlist.WishlistActivity
import com.example.shopeeclone.util.ContactDialog

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEditProfile.setOnClickListener {
            if (requireLogin()) startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.itemToPay.setOnClickListener { openOrders(OrderHistoryActivity.TAB_TO_PAY) }
        binding.itemToShip.setOnClickListener { openOrders(OrderHistoryActivity.TAB_TO_SHIP) }
        binding.itemShipping.setOnClickListener { openOrders(OrderHistoryActivity.TAB_SHIPPING) }
        binding.itemToReview.setOnClickListener { openOrders(OrderHistoryActivity.TAB_TO_REVIEW) }
        binding.tvViewAllOrders.setOnClickListener { openOrders(OrderHistoryActivity.TAB_ALL) }
    }

    /** รีเฟรชทุกครั้งที่กลับมาหน้านี้ (ชื่อ เหรียญ คูปอง ที่อยู่ ถูกใจ อาจเปลี่ยนจากหน้าอื่น) */
    override fun onResume() {
        super.onResume()
        showUserInfo()
        setupMenuRows()
    }

    private fun showUserInfo() {
        val user = UserRepository.getCurrentUser(requireContext())
        if (user != null) {
            binding.tvUserName.text = user.name
            binding.tvUserPhone.text = user.phone.take(3) + "xxxx" + user.phone.takeLast(3)
            binding.tvAvatarInitial.text = user.name.first().toString()
        } else {
            binding.tvUserName.text = "ผู้เยี่ยมชม"
            binding.tvUserPhone.text = "ยังไม่ได้เข้าสู่ระบบ"
            binding.tvAvatarInitial.text = "?"
        }
    }

    private fun setupMenuRows() {
        val ctx = requireContext()
        val used = RewardRepository.usedVoucherCodes(ctx)
        val vouchersLeft = RewardRepository.vouchers.count { it.code !in used }

        bindMenuRow(
            binding.rowAddress, android.R.drawable.ic_menu_myplaces, "ที่อยู่จัดส่งของฉัน",
            "${AddressBookRepository.getAll(ctx).size} ที่อยู่"
        ) { startActivity(Intent(ctx, AddressBookActivity::class.java)) }

        bindMenuRow(
            binding.rowCoins, android.R.drawable.ic_menu_compass, "Shopee Coins",
            "${RewardRepository.coinBalance(ctx)} เหรียญ"
        ) {
            startActivity(
                Intent(ctx, RewardsActivity::class.java)
                    .putExtra(RewardsActivity.EXTRA_MODE, RewardsActivity.MODE_COINS)
            )
        }

        bindMenuRow(
            binding.rowVoucher, android.R.drawable.ic_menu_set_as, "คูปองส่วนลดของฉัน",
            "ใช้ได้ $vouchersLeft ใบ"
        ) {
            startActivity(
                Intent(ctx, RewardsActivity::class.java)
                    .putExtra(RewardsActivity.EXTRA_MODE, RewardsActivity.MODE_VOUCHERS)
            )
        }

        bindMenuRow(
            binding.rowWishlist, android.R.drawable.btn_star_big_off, "สินค้าที่ถูกใจ",
            "${WishlistRepository.getIds(ctx).size} ชิ้น"
        ) { startActivity(Intent(ctx, WishlistActivity::class.java)) }

        bindMenuRow(
            binding.rowOrderHistory, android.R.drawable.ic_menu_recent_history, "ประวัติการสั่งซื้อ"
        ) { openOrders(OrderHistoryActivity.TAB_ALL) }

        bindMenuRow(
            binding.rowContactUs, android.R.drawable.ic_menu_call, "ติดต่อเรา / ศูนย์ช่วยเหลือ"
        ) { ContactDialog.show(ctx) }
    }

    private fun bindMenuRow(
        row: ItemProfileMenuRowBinding,
        iconRes: Int,
        title: String,
        subtitle: String? = null,
        onClick: () -> Unit
    ) {
        row.ivMenuIcon.setImageResource(iconRes)
        row.tvMenuTitle.text = title
        if (subtitle != null) {
            row.tvMenuSubtitle.text = subtitle
            row.tvMenuSubtitle.visibility = View.VISIBLE
        }
        row.root.setOnClickListener { onClick() }
    }

    private fun openOrders(tab: Int) {
        startActivity(
            Intent(requireContext(), OrderHistoryActivity::class.java)
                .putExtra(OrderHistoryActivity.EXTRA_TAB, tab)
        )
    }

    private fun requireLogin(): Boolean {
        if (UserRepository.isLoggedIn(requireContext())) return true
        Toast.makeText(requireContext(), "กรุณาเข้าสู่ระบบก่อนใช้งานฟีเจอร์นี้", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}