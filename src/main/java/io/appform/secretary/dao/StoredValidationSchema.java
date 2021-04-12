package io.appform.secretary.dao;

import io.appform.dropwizard.sharding.sharding.LookupKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

@Entity
@Table(name = "validation_schema",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"uuid"}),
        @UniqueConstraint(columnNames = {"name", "version"})}
        )
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoredValidationSchema {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private String id;

        @LookupKey
        @Column(name = "uuid")
        private String uuid;

        @Column(name = "name")
        private String name;

        @Column(name = "version")
        private int version;

        @Column(name = "active")
        private boolean active;

        @Column(name = "data",
                columnDefinition = "blob")
        private byte[] schema;

        @Column(name = "created",
                columnDefinition = "timestamp",
                updatable = false,
                insertable = false)
        @Generated(value = GenerationTime.INSERT)
        private Date created;

        @Column(name = "updated",
                columnDefinition = "timestamp default current_timestamp",
                updatable = false,
                insertable = false)
        private Date updated;
}
