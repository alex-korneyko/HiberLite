package ua.in.korneiko.hiberlite;

import ua.in.korneiko.hiberlite.annotations.Column;
import ua.in.korneiko.hiberlite.annotations.Entity;

@Entity
class JoinAuxEntity {

    @Column
    private Integer ownerId;

    @Column
    private Integer joinId;

    public JoinAuxEntity() {
    }

    public JoinAuxEntity(Integer ownerId, Integer joinId) {
        this.ownerId = ownerId;
        this.joinId = joinId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getJoinId() {
        return joinId;
    }

    public void setJoinId(Integer joinId) {
        this.joinId = joinId;
    }
}
