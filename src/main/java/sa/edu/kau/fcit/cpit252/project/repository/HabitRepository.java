package sa.edu.kau.fcit.cpit252.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sa.edu.kau.fcit.cpit252.project.entity.HabitEntity;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<HabitEntity, String> {

    List<HabitEntity> findByCategoryIgnoreCase(String category);
}
