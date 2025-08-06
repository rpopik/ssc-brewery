/*
 *  Copyright 2020 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package guru.sfg.brewery.web.mappers;

import guru.sfg.brewery.domain.BeerOrderLine;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.model.BeerOrderLineDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class BeerOrderLineMapperDecorator implements BeerOrderLineMapper {

    @Autowired
    @Qualifier("beerRepository")
    private BeerRepository beerRepository;

    @Autowired
    @Qualifier("delegate")
    private BeerOrderLineMapper delegate;


    @Override
    public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line) {
        BeerOrderLineDto orderLineDto = delegate.beerOrderLineToDto(line);
        orderLineDto.setBeerId(line.getBeer().getId());
        return orderLineDto;
    }

    @Override
    public BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto) {
        BeerOrderLine beerOrderLine = delegate.dtoToBeerOrderLine(dto);
        beerOrderLine.setBeer(beerRepository.findById(dto.getBeerId())
                .orElseThrow(() -> new RuntimeException("Beer not found: " + dto.getBeerId())));
        beerOrderLine.setQuantityAllocated(0);
        return beerOrderLine;
    }
}