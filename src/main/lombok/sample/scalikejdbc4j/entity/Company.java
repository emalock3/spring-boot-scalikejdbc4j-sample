package sample.scalikejdbc4j.entity;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {
    private Long id;
    private Optional<String> name;
}
