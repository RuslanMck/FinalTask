package dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItunesDto {

    private String artistName;
    private long artistId;
}
