package com.oh.register.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

@Entity(name = "Holiday")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Employee employee;

    private LocalDate startDate;

    private LocalDate finishDate;

    @ElementCollection
    private Map<LocalDate, LocalDate> localDateStorage = new TreeMap<>();
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