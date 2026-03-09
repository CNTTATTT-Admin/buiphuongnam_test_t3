package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.kurisu.mentormatch.entity.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {
}
