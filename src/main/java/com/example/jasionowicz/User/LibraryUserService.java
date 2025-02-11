package com.example.jasionowicz.User;

import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class LibraryUserService {
    private LibraryUserRepository libraryUserRepository;

    public LibraryUserService(LibraryUserRepository libraryUserRepository) {
        this.libraryUserRepository = libraryUserRepository;
    }

    public LibraryUser save(LibraryUser libraryUser) {
        return libraryUserRepository.save(libraryUser);
    }
    public LibraryUser getLibraryUser(Long id) {
    return   libraryUserRepository.getReferenceById(id);
    }

    public List<LibraryUser> getLibraryUsers() {
        return libraryUserRepository.findAll();
    }
    public void deleteLibraryUser(Long id) {
        libraryUserRepository.deleteById(id);
    }

//    public void updateLibraryUser(Long id, LibraryUser libraryUser) {
//        LibraryUser oldLibraryUser = libraryUserRepository.getReferenceById(id);
//        oldLibraryUser.setName(libraryUser.getName());
//        oldLibraryUser.setEmail(libraryUser.getEmail());
//    }

}
