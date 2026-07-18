package gr.grodov.grsso.domain.mapper;

public interface Mapper<DB, DTO> {
    DTO fromDB(DB entity);
    DB toDB(DTO dto);
}
