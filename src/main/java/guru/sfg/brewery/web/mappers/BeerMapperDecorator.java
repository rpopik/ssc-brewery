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

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerInventory;
import guru.sfg.brewery.web.model.BeerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class BeerMapperDecorator implements BeerMapper{

    @Autowired
    @Qualifier("delegate")
    private BeerMapper delegate;

    @Override
    public BeerDto beerToBeerDto(Beer beer) {

        BeerDto dto = delegate.beerToBeerDto(beer);

        if(beer.getBeerInventory() != null && beer.getBeerInventory().size() > 0) {
            dto.setQuantityOnHand(beer.getBeerInventory()
                    .stream().map(BeerInventory::getQuantityOnHand)
                    .reduce(0, Integer::sum));

        }

        return dto;
    }
}