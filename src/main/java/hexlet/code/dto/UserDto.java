package hexlet.code.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UserDto {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private LocalDateTime createdAt;
}
