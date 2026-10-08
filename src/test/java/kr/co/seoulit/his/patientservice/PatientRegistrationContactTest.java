package kr.co.seoulit.his.patientservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patient.dto.PatientDto;
import kr.co.seoulit.his.patientservice.patient.entity.PatientEntity;
import kr.co.seoulit.his.patientservice.patient.repository.PatientRepository;
import kr.co.seoulit.his.patientservice.patient.service.impl.PatientServiceImpl;
import kr.co.seoulit.his.patientservice.patientcontact.dto.PatientContactCreateRequestDto;
import kr.co.seoulit.his.patientservice.patientcontact.repository.PatientContactRepository;
import kr.co.seoulit.his.patientservice.patientcontact.service.PatientContactService;
import kr.co.seoulit.his.patientservice.patientsafety.repository.PatientSafetyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PatientRegistrationContactTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientContactRepository patientContactRepository;

    @Mock
    private PatientContactService patientContactService;

    @Mock
    private PatientSafetyRepository patientSafetyRepository;

    private PatientServiceImpl patientService;

    @BeforeEach
    void setUp() {
        patientService = new PatientServiceImpl(
                patientRepository,
                patientContactRepository,
                patientContactService,
                patientSafetyRepository);
    }

    @Test
    void createPatientSavesRegistrationAddressAndPhoneAsContact() {
        UUID patientId = UUID.randomUUID();
        PatientDto request = new PatientDto();
        request.setPatientName("Test Patient");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setResidentRegNo("9001011234567");
        request.setGenderCd("01");
        request.setTempPatientYn("N");
        request.setZipCode("06236");
        request.setAddress("Seoul Test Road 123");
        request.setAddressDetail("Room 401");
        request.setPhoneNo("01012345678");

        when(patientRepository.existsByResidentRegNoAndMergedToPatientIdIsNull(
                request.getResidentRegNo())).thenReturn(false);
        when(patientRepository.save(any(PatientEntity.class))).thenAnswer(invocation -> {
            PatientEntity patient = invocation.getArgument(0);
            patient.setPatientId(patientId);
            return patient;
        });

        patientService.createPatient(request);

        ArgumentCaptor<PatientContactCreateRequestDto> contactRequest =
                ArgumentCaptor.forClass(PatientContactCreateRequestDto.class);
        verify(patientContactService).createContact(eq(patientId), contactRequest.capture());
        assertEquals("06236", contactRequest.getValue().zipCode());
        assertEquals("Seoul Test Road 123", contactRequest.getValue().address());
        assertEquals("Room 401", contactRequest.getValue().addressDetail());
        assertEquals("01012345678", contactRequest.getValue().phoneNo());
    }
}
