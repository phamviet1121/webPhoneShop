package com.example.phone_shop.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.phone_shop.models.ContactMessage;
import com.example.phone_shop.repositories.ContactRepository;

@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;
    
    public void save(ContactMessage contactMessage) {
        contactRepository.save(contactMessage);
    }

    // 2. Lấy toàn bộ danh sách (Chỉ lấy những tin CHƯA bị xóa mềm)
    public List<ContactMessage> getAllActiveContacts() {
        // Gọi hàm custom trong Repository (cần khai báo bên Repository)
        return contactRepository.findByDeletedAtIsNullOrderByCreatedAtDesc();
    }

    // 3. Lấy chi tiết 1 tin nhắn theo ID
    public ContactMessage getContactById(Integer id) {
        return contactRepository.findById(id).orElse(null);
    }

    // 4. Xóa mềm (Cập nhật thời gian xóa chứ không xóa khỏi DB)
    public void softDeleteContact(Integer id) {
        Optional<ContactMessage> contactOpt = contactRepository.findById(id);
        if (contactOpt.isPresent()) {
            ContactMessage contact = contactOpt.get();
            contact.setDeletedAt(LocalDateTime.now()); // Set thời gian xóa
            contactRepository.save(contact); // Lưu lại
        }
    }

    // 5. Đổi trạng thái (0: Chưa xem -> 1: Đã xem -> 2: Đã xử lý)
    public void updateStatus(Integer id, Integer newStatus) {
        Optional<ContactMessage> contactOpt = contactRepository.findById(id);
        if (contactOpt.isPresent()) {
            ContactMessage contact = contactOpt.get();
            contact.setStatus(newStatus);
            contactRepository.save(contact);
        }
    }
    
    // 6. Thống kê số tin chưa đọc (để hiển thị chuông thông báo chẳng hạn)
    public long countUnread() {
        return contactRepository.countByStatusAndDeletedAtIsNull(0);
    }
}
