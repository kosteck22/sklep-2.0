package pl.zielona_baza.admin.brand;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.zielona_baza.admin.AmazonS3Util;
import pl.zielona_baza.admin.brand.BrandNotFoundException;
import pl.zielona_baza.admin.brand.BrandService;
import pl.zielona_baza.admin.category.CategoryService;
import pl.zielona_baza.admin.exception.ValidationException;
import pl.zielona_baza.common.entity.Brand;
import pl.zielona_baza.common.entity.Category;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/brands")
public class BrandController {
    private final BrandService brandService;
    private final CategoryService categoryService;

    public BrandController(BrandService brandService, CategoryService categoryService) {
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listFirstPage(Model model) {
        return listByPage(1, "name", "asc", null, 20, model);
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(@PathVariable("pageNum") Integer pageNum,
                             @RequestParam(name = "sortField", required = false) String sortField,
                             @RequestParam(name = "sortDir", required = false) String sortDir,
                             @RequestParam(name = "keyword", required = false) String keyword,
                             @RequestParam(name = "limit", required = false) Integer limit,
                             Model model) {
        brandService.listByPage(pageNum, sortField, sortDir, limit, keyword, model);

        return "brands/brands";
    }

    @GetMapping("/new")
    public String newBrand(Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("brand", new Brand());
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create new brand");

        return "brands/brand_form";
    }

    @PostMapping("/save")
    public String saveBrand(Brand brand,
                            @RequestParam("fileImage") MultipartFile multipartFile,
                            RedirectAttributes redirectAttributes,
                            Model model) throws IOException {
        try {
            brandService.save(brand, multipartFile);
        } catch (ValidationException ex) {
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("brand", brand);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Create new brand");
            model.addAttribute("message", ex.getMessage());

            return "brands/brand_form";
        }
        redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully.");
        return "redirect:/brands";
    }

    @GetMapping("/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            Brand brand = brandService.getById(id);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("brand", brand);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Create new brand");

            return "brands/brand_form";
        } catch (BrandNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/brands";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id") Integer id,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            brandService.delete(id);

            redirectAttributes.addFlashAttribute("message", "The brand with ID " + id +
                    " has been deleted successfully");
        } catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/brands";
    }
}