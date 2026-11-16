SET foreign_key_checks = 0;

DROP TABLE IF EXISTS Treatments;
DROP TABLE IF EXISTS Appointments;
DROP TABLE IF EXISTS Vet;
DROP TABLE IF EXISTS Pets;

SET foreign_key_checks = 1;

CREATE TABLE Pets (
    pet_id INT NOT NULL PRIMARY KEY,
    owner_name VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    owner_phone CHAR(10) NOT NULL,
    pet_name VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    species VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    gender TINYINT NOT NULL,
    is_neutured TINYINT NOT NULL,
    breed VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    birth_date DATE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Vet (
    vet_id INT NOT NULL PRIMARY KEY,
    name_surname VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    specialization VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    phone CHAR(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Appointments (
    appointment_id INT NOT NULL PRIMARY KEY,
    pet_id INT NOT NULL,
    vet_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    FOREIGN KEY (pet_id) REFERENCES Pets(pet_id),
    FOREIGN KEY (vet_id) REFERENCES Vet(vet_id),
    -- Aynı doktorun aynı gün ve saatte birden fazla randevu almasını engelleyen kısıt:
    CONSTRAINT unique_vet_schedule UNIQUE (vet_id, appointment_date, appointment_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Treatments (
    treatment_id INT NOT NULL PRIMARY KEY,
    appointment_id INT NOT NULL,
    exam_type VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    summary VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    FOREIGN KEY (appointment_id) REFERENCES Appointments(appointment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Çoklu Veteriner Kayıtları
INSERT INTO Vet (vet_id, name_surname, specialization, phone) VALUES 
(1, 'Dr. Can Öztürk', 'Surgery', '5551112233'),
(2, 'Dr. Ayşe Demir', 'Internal Medicine', '5552223344'),
(3, 'Dr. Mehmet Kaya', 'Dermatology', '5553334455'),
(4, 'Dr. Zeynep Çelik', 'Cardiology', '5554445566');

select * from pets;