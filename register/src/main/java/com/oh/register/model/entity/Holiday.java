package com.oh.register.model.entity;

import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@Entity(name = "Holiday")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="holiday_id")
    private Long id;

    @OneToOne
    private Employee employee;

    private LocalDate startDate;

    private LocalDate finishDate;

    @ElementCollection
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "holiday_id")
    @Cascade(value = {CascadeType.ALL})
    @CollectionTable(name = "holiday_map", joinColumns = @JoinColumn(name = "holiday_id"))
    @MapKeyColumn(name = "holiday_map_key")
    @Column(name = "holiday_map_value")
    private Map<LocalDate, LocalDate> localDateStorage = new HashMap<>();
}
/*
A munkatárshoz fel kell tudni venni, hogy mettől meddig van szabadságon (a szabadság első és utolsó napja legyen rögzítve).
Lehessen törölni szabadságot úgy, hogy megadom a kezdő és a végdátumot (figyelem, ez feltétlen a szabadság teljes törlésével azonos).
Üres intervallumot (a kezdő dátum nagyobb, mint a végdátum nem tárolunk).
Le kell tudni kérdezni
- egy munkatárs egy adott időszakban (dátumtól dátumig) kivett összes szabadság intervallumát,
- egy munkatárs egy adott időszakban (dátumtól dátumig) hány napot dolgozott,
- egy munkatárs egy adott év adott hónapjában hány napot dolgozott.

 */