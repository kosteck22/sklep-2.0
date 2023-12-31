package pl.zielona_baza.admin.paging;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import pl.zielona_baza.admin.DTOMapper;

@Getter
@Setter
public class PagingAndSortingHelper {

    private String listName;
    private String sortField;
    private String sortDir;
    private String reverseSortDir;
    private String keyword;
    private Integer limit;
    private Integer pageNum;

    public PagingAndSortingHelper(String listName,
                                  String sortField,
                                  String sortDir,
                                  String keyword,
                                  Integer limit,
                                  Integer pageNum) {
        this.listName = listName;
        this.sortField = sortField;
        this.sortDir = sortDir;
        reverseSortDir = sortDir.equals("desc") ? "asc" : "desc";
        this.keyword = keyword;
        this.limit = limit;
        this.pageNum = pageNum;
    }

    public void listEntities(SearchRepository<?, Integer> repository, Model model) {
        listEntities(repository, model, null);
    }

    public void listEntities(SearchRepository<?, Integer> repository, Model model, DTOMapper dtoMapper) {
        Pageable pageable = createPageable(pageNum);
        Page<?> page;

        if (keyword != null) {
            page = repository.findAll(keyword, pageable);
        } else {
            page = repository.findAll(pageable);
        }
        updateModelAttributes(pageNum, page, model, dtoMapper);
    }

    public void updateModelAttributes(int pageNum, Page<?> page, Model model) {
        updateModelAttributes(pageNum, page, model, null);
    }

    public void updateModelAttributes(int pageNum, Page<?> page, Model model, DTOMapper dtoMapper) {
        int pageSize = page.getSize();
        long totalItems = page.getTotalElements();
        long startCount = (long) (pageNum - 1) * pageSize + 1;
        long endCount = startCount + pageSize - 1;
        if(endCount > totalItems) {
            endCount = totalItems;
        }

        model.addAttribute("sortField", sortField);
        model.addAttribute("limit", limit);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", totalItems);

        if (dtoMapper == null) {
            model.addAttribute(listName, page.getContent());
        } else {
            model.addAttribute(listName, dtoMapper.convert(page.getContent()));
        }
    }

    public Pageable createPageable(int pageNum) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("desc") ? sort.descending() : sort.ascending();

        return PageRequest.of(pageNum - 1, limit, sort);
    }
}