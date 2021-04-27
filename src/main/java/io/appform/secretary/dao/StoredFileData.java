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
@Table(name = "file_data",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"uuid"}),
                @UniqueConstraint(columnNames = {"hashsum"})
        })
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoredFileData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private String id;

    @LookupKey
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "hashsum")
    private String hash;

    @Column(name = "user")
    private String user;

    @Column(name = "workflow")
    private String workflow;

    @Column(name = "state")
    private String state;

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
