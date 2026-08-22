package gr.grodov.grsso.common.mapper;

public interface Mapper<DB, DTO> {
    DTO fromDB(DB entity);
    DB toDB(DTO dto);
}
