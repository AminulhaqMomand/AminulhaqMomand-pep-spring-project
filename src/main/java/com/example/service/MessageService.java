package com.example.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

import javafx.print.Collation;
@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Message createMessage(Message message) {
        if (message.getMessageText() == null || message.getMessageText().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text cannot be blank");
        }
        if (message.getMessageText().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text cannot be over 255 characters");
        }
        if (message.getPostedBy() == null || !accountRepository.findById(message.getPostedBy()).isPresent())  {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PostedBy must refer to a real, existing user");
        }
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(Integer messageId) {
        return messageRepository.findById(messageId);
    }
    public boolean deleteMessage(Integer messageId) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (messageOptional.isPresent()) {
            messageRepository.deleteById(messageId);
            return true;
        } else {
            return false;
        }
    }
    

    public int updateMessage(Integer messageId, String newMessageText) {
        if (newMessageText == null || newMessageText.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text cannot be blank");
        }
        if (newMessageText.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text cannot be over 255 characters");
        }

        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            message.setMessageText(newMessageText);
            messageRepository.save(message);
            return 1; // Indicate that one row was updated
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message with given ID not found");
        }
    }
    public List<Message> getMessagesByAccountId(Integer accountId) {
       
        if (accountRepository.findById(accountId).isPresent()) {
            return messageRepository.findByPostedBy(accountId);
        } else {
            
            return Collections.emptyList();
        }
    }
}
