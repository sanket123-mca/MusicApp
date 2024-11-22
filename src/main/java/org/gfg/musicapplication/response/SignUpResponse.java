package org.gfg.musicapplication.response;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpResponse{

    private Integer userId;

    private String email;

    private String message;

}
