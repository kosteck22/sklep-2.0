package pl.zielona_baza.site.address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.zielona_baza.common.entity.Address;
import pl.zielona_baza.common.entity.Country;
import pl.zielona_baza.common.entity.Customer;
import pl.zielona_baza.site.ControllerHelper;
import pl.zielona_baza.site.Utility;
import pl.zielona_baza.site.customer.CustomerNotFoundException;
import pl.zielona_baza.site.customer.CustomerService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/address_book")
public class AddressController {
    private final AddressService addressService;
    private final CustomerService customerService;
    private final ControllerHelper controllerHelper;

    public AddressController(AddressService addressService, CustomerService customerService, ControllerHelper controllerHelper) {
        this.addressService = addressService;
        this.customerService = customerService;
        this.controllerHelper = controllerHelper;
    }

    @GetMapping
    public String showAddressBook(Model model, HttpServletRequest request,
                                  @RequestParam(name = "redirect", required = false) String redirect) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request).get();
        List<Address> addresses = addressService.listAddressBook(customer);

        boolean usePrimaryAddressAsDefault = true;

        for (Address address : addresses) {
            if (address.isDefaultForShipping()) {
                usePrimaryAddressAsDefault = false;
                break;
            }
        }

        model.addAttribute("listAddresses", addresses);
        model.addAttribute("customer", customer);
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.addAttribute("redirect", redirect);

        return "address_book/addresses";
    }

    @GetMapping("/new")
    public String newAddress(Model model) {
        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("listCountries", listCountries);
        model.addAttribute("address", new Address());
        model.addAttribute("pageTitle", "Add New Address");

        return "address_book/address_form";
    }

    @PostMapping("/save")
    public String saveAddress(Address address,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes,
                              @RequestParam(name = "redirect", required = false) String redirect) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request).get();

        address.setCustomer(customer);
        addressService.save(address);

        String redirectURL = "redirect:/address_book";

        if ("checkout".equals(redirect)) {
            redirectURL += "?redirect=checkout";
        }
        redirectAttributes.addFlashAttribute("message", "The address has been saved successfully.");

        return redirectURL;
    }

    @GetMapping("/edit/{id}")
    public String editAddress(@PathVariable("id") Integer addressId,
                              Model model,
                              HttpServletRequest request,
                              @RequestParam(name = "redirect", required = false) String redirect) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request).get();

        List<Country> listCountries = customerService.listAllCountries();

        Address address =  addressService.get(addressId, customer.getId());

        model.addAttribute("address", address);
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Edit Address");
        model.addAttribute("redirect", redirect);

        return "address_book/address_form";
    }

    @GetMapping("/delete/{id}")
    public String deleteAddress(@PathVariable("id") Integer addressId,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request).get();
        addressService.delete(addressId, customer.getId());

        redirectAttributes.addFlashAttribute("message", "The address has been deleted");

        return "redirect:/address_book";
    }

    @GetMapping("/default/{id}")
    public String setDefaultAddress(@PathVariable("id") Integer addressId,
                                    HttpServletRequest request,
                                    @RequestParam(name = "redirect", required = false) String redirect) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request).get();
        addressService.setDefaultAddress(addressId, customer.getId());

        String redirectURL = "redirect:/address_book";

        if ("cart".equals(redirect)) {
            redirectURL = "redirect:/cart";
        } else if ("checkout".equals(redirect)) {
            redirectURL = "redirect:/checkout";
        }

        return redirectURL;
    }
}