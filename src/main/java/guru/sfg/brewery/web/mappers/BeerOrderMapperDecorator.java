package guru.sfg.brewery.web.mappers;

import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.web.model.BeerOrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class BeerOrderMapperDecorator implements BeerOrderMapper {

    @Autowired
    @Qualifier("delegate")
    private BeerOrderMapper delegate;

    @Override
    public BeerOrderDto beerOrderToDto(BeerOrder beerOrder) {
        BeerOrderDto beerDto = delegate.beerOrderToDto(beerOrder);

        // Set properties from beerOrder to dto
        if (beerOrder.getId() != null) {
            beerDto.setId(beerOrder.getId());
        }

        if (beerOrder.getCustomer() != null) {
            beerDto.setCustomerId(beerOrder.getCustomer().getId());
        }

        return beerDto;
    }
}