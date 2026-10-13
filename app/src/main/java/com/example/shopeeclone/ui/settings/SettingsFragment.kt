package com.example.shopeeclone.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.shopeeclone.databinding.FragmentSettingsBinding
import com.example.shopeeclone.databinding.ItemProfileMenuRowBinding
import com.example.shopeeclone.databinding.ItemSettingsSwitchRowBinding
import com.example.shopeeclone.repository.SettingsRepository
import com.example.shopeeclone.repository.UserRepository
import com.example.shopeeclone.ui.address.AddressBookActivity
import com.example.shopeeclone.ui.auth.LoginActivity
import com.example.shopeeclone.ui.info.InfoActivity
import com.example.shopeeclone.ui.profile.EditProfileActivity

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAccountSection()
        setupNotificationSection()
        setupGeneralSection()
        setupAboutSection()
        setupLogout()
        showAppVersion()
    }

    private fun setupAccountSection() {
        val ctx = requireContext()

        bindMenuRow(binding.rowEditProfile, android.R.drawable.ic_menu_edit, "แก้ไขโปรไฟล์") {
            if (requireLogin()) startActivity(Intent(ctx, EditProfileActivity::class.java))
        }
        bindMenuRow(binding.rowChangePassword, android.R.drawable.ic_lock_lock, "เปลี่ยนรหัสผ่าน") {
            if (requireLogin()) startActivity(Intent(ctx, ChangePasswordActivity::class.java))
        }
        bindMenuRow(binding.rowShippingAddress, android.R.drawable.ic_menu_myplaces, "ที่อยู่จัดส่ง") {
            startActivity(Intent(ctx, AddressBookActivity::class.java))
        }
        bindMenuRow(
            binding.rowPaymentMethods, android.R.drawable.ic_menu_sort_by_size, "วิธีชำระเงินเริ่มต้น"
        ) { showPaymentDialog() }
        refreshPaymentSubtitle()
    }

    private fun refreshPaymentSubtitle() {
        val index = SettingsRepository.getDefaultPayment(requireContext())
            .coerceIn(0, SettingsRepository.PAYMENT_SHORT_LABELS.lastIndex)
        binding.rowPaymentMethods.tvMenuSubtitle.text = SettingsRepository.PAYMENT_SHORT_LABELS[index]
        binding.rowPaymentMethods.tvMenuSubtitle.visibility = View.VISIBLE
    }

    private fun showPaymentDialog() {
        val ctx = requireContext()
        AlertDialog.Builder(ctx)
            .setTitle("วิธีชำระเงินเริ่มต้น")
            .setSingleChoiceItems(
                SettingsRepository.PAYMENT_LABELS.toTypedArray(),
                SettingsRepository.getDefaultPayment(ctx)
            ) { dialog, which ->
                SettingsRepository.setDefaultPayment(ctx, which)
                refreshPaymentSubtitle()
                dialog.dismiss()
            }
            .setNegativeButton("ยกเลิก", null)
            .show()
    }

    private fun setupNotificationSection() {
        bindPrefSwitch(
            binding.rowNotifyPromo, "การแจ้งเตือนโปรโมชัน", "รับข่าวสารส่วนลดและดีลพิเศษ",
            SettingsRepository.KEY_NOTIFY_PROMO, true
        )
        bindPrefSwitch(
            binding.rowNotifyOrder, "การแจ้งเตือนคำสั่งซื้อ", "อัปเดตสถานะการจัดส่ง",
            SettingsRepository.KEY_NOTIFY_ORDER, true
        )
        bindPrefSwitch(
            binding.rowNotifyChat, "การแจ้งเตือนข้อความ", "แจ้งเตือนเมื่อมีข้อความจากผู้ขาย",
            SettingsRepository.KEY_NOTIFY_CHAT, false
        )
    }

    private fun setupGeneralSection() {
        bindMenuRow(
            binding.rowLanguage, android.R.drawable.ic_menu_sort_alphabetically, "ภาษา", "ไทย"
        ) {
            Toast.makeText(requireContext(), "ขณะนี้รองรับภาษาไทยเท่านั้น", Toast.LENGTH_SHORT).show()
        }

        val ctx = requireContext()
        bindSwitchRow(
            binding.rowDarkMode, "โหมดมืด", "ปรับธีมแอปให้ถนอมสายตา",
            SettingsRepository.isDarkMode(ctx)
        ) { enabled ->
            SettingsRepository.setBoolean(ctx, SettingsRepository.KEY_DARK_MODE, enabled)
            AppCompatDelegate.setDefaultNightMode(
                if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun setupAboutSection() {
        val ctx = requireContext()
        bindMenuRow(binding.rowHelpCenter, android.R.drawable.ic_menu_help, "ศูนย์ช่วยเหลือ") {
            openInfo(InfoActivity.TYPE_HELP)
        }
        bindMenuRow(binding.rowPrivacyPolicy, android.R.drawable.ic_menu_info_details, "นโยบายความเป็นส่วนตัว") {
            openInfo(InfoActivity.TYPE_PRIVACY)
        }
        bindMenuRow(binding.rowTermsOfUse, android.R.drawable.ic_menu_agenda, "ข้อกำหนดการใช้งาน") {
            openInfo(InfoActivity.TYPE_TERMS)
        }
    }

    private fun openInfo(type: String) {
        startActivity(
            Intent(requireContext(), InfoActivity::class.java).putExtra(InfoActivity.EXTRA_TYPE, type)
        )
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("ออกจากระบบ")
                .setMessage("คุณต้องการออกจากระบบใช่หรือไม่?")
                .setPositiveButton("ออกจากระบบ") { dialog, _ ->
                    dialog.dismiss()
                    UserRepository.logout(requireContext())
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("ยกเลิก") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    @Suppress("DEPRECATION")
    private fun showAppVersion() {
        val ctx = requireContext()
        val version = try {
            ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
        } catch (e: Exception) {
            null
        }
        if (version != null) binding.tvAppVersion.text = "เวอร์ชันแอป $version"
    }

    private fun requireLogin(): Boolean {
        if (UserRepository.isLoggedIn(requireContext())) return true
        Toast.makeText(requireContext(), "กรุณาเข้าสู่ระบบก่อนใช้งานฟีเจอร์นี้", Toast.LENGTH_SHORT).show()
        return false
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

    private fun bindSwitchRow(
        row: ItemSettingsSwitchRowBinding,
        title: String,
        subtitle: String,
        checked: Boolean,
        onToggle: (Boolean) -> Unit
    ) {
        row.tvSwitchTitle.text = title
        row.tvSwitchSubtitle.text = subtitle
        row.switchToggle.isChecked = checked
        row.switchToggle.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            onToggle(isChecked)
        })
    }

    private fun bindPrefSwitch(
        row: ItemSettingsSwitchRowBinding,
        title: String,
        subtitle: String,
        key: String,
        default: Boolean
    ) {
        val ctx = requireContext()
        bindSwitchRow(row, title, subtitle, SettingsRepository.getBoolean(ctx, key, default)) {
            SettingsRepository.setBoolean(ctx, key, it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}