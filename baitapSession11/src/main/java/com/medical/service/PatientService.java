package com.medical.service;

import com.example.baitapsession11.dto.PatientDTO;
import com.example.baitapsession11.model.Patient;
import com.example.baitapsession11.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class PatientService {
    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional
    public Patient addPatient(PatientDTO patient) {
        if (patient.getAge() > 120) {
            log.warn("Tuổi bệnh nhân quá cao: name={}, age={}", patient.getName(), patient.getAge());
        }
        Patient entity = new Patient();
        entity.setName(patient.getName());
        entity.setAge(patient.getAge());
        Patient saved = patientRepository.save(entity);
        log.info("Đã tiếp nhận bệnh nhân: {}", saved.getName());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Patient> findPatients(String name) {
        String keyword = name.trim();
        log.trace("Tìm kiếm bệnh nhân với name={}", keyword);
        List<Patient> patients = patientRepository.findByNameContainingIgnoreCase(keyword);
        log.debug("Tìm thấy {} bệnh nhân", patients.size());
        return patients;
    }
}
