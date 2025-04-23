package com.familyplanner.familyplanner.service;
import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.repository.FamilyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class FamilyService {

    private final FamilyRepository familyRepository;

    @Autowired
    public FamilyService(FamilyRepository familyRepository) {
        this.familyRepository = familyRepository;
    }

    public Family getFamilyById(Long id) {
        return familyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Family not found with id: " + id));
    }

    public Family getFamilyByCode(String code) {
        try {
            Long familyId = Long.parseLong(code);
            return getFamilyById(familyId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Family saveFamily(Family family) {
        return familyRepository.save(family);
    }

    public void deleteFamily(Long id) {
        familyRepository.deleteById(id);
    }
}