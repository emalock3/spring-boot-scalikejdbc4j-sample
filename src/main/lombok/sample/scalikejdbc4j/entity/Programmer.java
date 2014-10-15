package sample.scalikejdbc4j.entity;

import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Programmer {
    private Long id;
    private String gitHubName;
    private Optional<String> realName;
    private Optional<Long> companyId;
    private Optional<Company> company;
    private ZonedDateTime createdAt;
}
