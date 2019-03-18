package ro.vitoc.licenta.core.converter;

import ro.vitoc.licenta.core.dto.BaseDto;
import ro.vitoc.licenta.core.model.BaseEntity;

public interface Converter<Model extends BaseEntity<Long>, Dto extends BaseDto> {

    Model convertDtoToModel(Dto dto);

    Dto convertModelToDto(Model model);

}

