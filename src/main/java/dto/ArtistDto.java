package dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArtistDto {

    private String artistName;
    private long artistId;
}
