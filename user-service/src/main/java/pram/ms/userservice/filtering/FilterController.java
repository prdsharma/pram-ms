package pram.ms.userservice.filtering;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FilterController {
    /**
     * Dynamic field filtering example
     */
    @GetMapping("/some-bean")
    public MappingJacksonValue getSomeBean() {

        SomeBean someBean = new SomeBean("field1", "field2", "field3");

        SimpleBeanPropertyFilter propertyFilter = SimpleBeanPropertyFilter
                .filterOutAllExcept("field2","field3");

        FilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("SomeBeanFilter", propertyFilter);

        MappingJacksonValue mapping = new MappingJacksonValue(someBean);
        mapping.setFilters(filterProvider);

        return  mapping;
    }
}
