package dk.cphbusiness.dtos;

import dk.cphbusiness.persistence.model.Trip;
import dk.cphbusiness.utils.IIdProvider;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO implements IIdProvider<Long> {
    private Long id;
    private LocalDateTime starttime;
    private LocalDateTime endtime;
    private double longitude;
    private double latitude;
    private String name;
    private double price;
    private Trip.TripCategory category;
    private GuideDTO guide;
    private Set<PackingItemDTO> packingItems;

    public TripDTO(Long id, LocalDateTime of, LocalDateTime of1, double v, double v1, String day, double v2, long l, Trip.TripCategory category, GuideDTO guide1) {
        this.id = id;
        this.starttime = of;
        this.endtime = of1;
        this.longitude = v;
        this.latitude = v1;
        this.name = day;
        this.price = v2;
        this.category = category;
        this.guide = guide1;
    }

    public TripDTO( LocalDateTime of, LocalDateTime of1, double v, double v1, String day, double v2, long l, Trip.TripCategory category, GuideDTO guide1) {
        this.starttime = of;
        this.endtime = of1;
        this.longitude = v;
        this.latitude = v1;
        this.name = day;
        this.price = v2;
        this.category = category;
        this.guide = guide1;
    }
    public TripDTO(Trip trip) {
        this.id = trip.getId();
        this.starttime = trip.getStartTime();
        this.endtime = trip.getEndTime();
        this.longitude = trip.getLongitude();
        this.latitude = trip.getLatitude();
        this.name = trip.getName();
        this.price = trip.getPrice();
        this.category = trip.getCategory();
        if(trip.getGuide() != null) this.guide = new GuideDTO(trip.getGuide());
    }

    public static Trip toEntity(TripDTO tripDTO) {
        return Trip.builder()
                .id(tripDTO.getId())
                .startTime(tripDTO.getStarttime())
                .endTime(tripDTO.getEndtime())
                .longitude(tripDTO.getLongitude())
                .latitude(tripDTO.getLatitude())
                .name(tripDTO.getName())
                .price(tripDTO.getPrice())
                .category(tripDTO.getCategory())
                .build();
    }

    public enum Category {
        BEACH,
        CITY,
        FOREST,
        LAKE,
        SEA,
        SNOW
    }
}
