package com.jvpereira.doclens.model.template;

import com.jvpereira.doclens.model.field.Field;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="templates")
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name= "tag", nullable = false, unique = true)
    private String tag;
    @Column(name= "name")
    private String name;
    @Column(name= "description", length = 1024)
    private String description;
    @Column(name= "prop_hints", length = 2048)
    private String propHints;
    @Enumerated(EnumType.STRING)
    private TemplateStatus status;
    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Field> fields;
}
