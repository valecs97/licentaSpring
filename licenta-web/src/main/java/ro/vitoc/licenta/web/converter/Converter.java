package ro.vitoc.licenta.web.converter;

import ro.vitoc.licenta.core.model.BaseEntity;
import ro.vitoc.licenta.web.dto.BaseDto;

public interface Converter<Model extends BaseEntity<Long>, Dto extends BaseDto> {

    Model convertDtoToModel(Dto dto);

    Dto convertModelToDto(Model model);

}

