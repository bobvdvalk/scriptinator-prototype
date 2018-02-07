/*
 * Copyright © 2018 Thomas Biesaart (thomas.biesaart@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.chapp.scriptinator.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@JsonPropertyOrder({
        "url",
        "pageNumber",
        "hasItems",
        "maxPageNumber",
        "next",
        "hasNext",
        "previous",
        "hasPrevious",
        "totalItemCount",
        "itemCount",
        "items"
})
public class PageResult<T> {
    public static final String SIZE_PARAMETER = "size";
    public static final int SIZE_PARAMETER_DEFAULT = 20;
    public static final String PAGE_PARAMETER = "page";
    public static final int PAGE_PARAMETER_DEFAULT = 1;
    private final Link next;
    private final Link previous;
    private final long totalItemCount;
    private final List<T> items;
    private final Link url;
    private final int pageNumber;
    private final int maxPageNumber;

    private PageResult(Link next, Link previous, long totalItemCount, List<T> items, Link url, int pageNumber, int maxPageNumber) {
        this.next = next;
        this.previous = previous;
        this.totalItemCount = totalItemCount;
        this.items = items;
        this.url = url;
        this.pageNumber = pageNumber;
        this.maxPageNumber = maxPageNumber;
    }

    public static <T> PageResult<T> of(Link url, Page<T> page) {
        return of(
                url,
                page.getTotalElements(),
                page.getContent(),
                page.getSize(),
                page.getNumber() + 1,
                page.getTotalPages()
        );
    }

    public static <T> PageResult<T> of(Link url, long totalItemCount, List<T> items, int pageSize, int pageNumber, int maxPageNumber) {
        Link self = url
                .withParameter(PAGE_PARAMETER, pageNumber)
                .withParameter(SIZE_PARAMETER, pageSize);

        return new PageResult<>(
                pageNumber < maxPageNumber ? self.withParameter("page", pageNumber + 1) : null,
                pageNumber > 1 ? self.withParameter("page", pageNumber - 1) : null,
                totalItemCount,
                items,
                self,
                pageNumber,
                maxPageNumber
        );
    }

    public static PageRequest request(HttpServletRequest request) {
        String sizeParam = request.getParameter(SIZE_PARAMETER);
        int size = StringUtils.isEmpty(sizeParam) ? SIZE_PARAMETER_DEFAULT : Integer.parseInt(sizeParam);

        String pageParam = request.getParameter(PAGE_PARAMETER);
        int page = StringUtils.isEmpty(pageParam) ? PAGE_PARAMETER_DEFAULT : Integer.parseInt(pageParam);

        return new PageRequest(
                page - 1,
                size
        );
    }

    public Link getNext() {
        return next;
    }

    public Link getPrevious() {
        return previous;
    }

    public long getTotalItemCount() {
        return totalItemCount;
    }

    public Link getUrl() {
        return url;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getMaxPageNumber() {
        return maxPageNumber;
    }

    public int getItemCount() {
        return items.size();

    }

    public boolean isHasNext() {
        return next != null;
    }

    public boolean isHasItems() {
        return items.size() > 0;
    }

    public boolean isHasPrevious() {
        return previous != null;
    }

    public List<T> getItems() {
        return items;
    }
}
