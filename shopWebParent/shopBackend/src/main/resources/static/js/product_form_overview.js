
 dropdownBrands = $("#brand");
  dropdownCategories = $("#category");

  $(document).ready(function() {

    $("#shortDescription").richText();
    $("#fullDescription").richText();

    dropdownBrands.change(function() {
      dropdownCategories.empty();
      getCategories();
    });

    getCategoriesForNewForm();
  });

  function getCategoriesForNewForm() {
    catIdField = $("#categoryId");
    editMode = false;

    if (catIdField.length) {
        editMode = true;
    }

    if (!editMode) getCategories();
  }

  function getCategories() {
    brandId = dropdownBrands.val();
    url = brandModuleURL + "/" + brandId + "/categories";

    $.get(url, function(responseJson) {
      $.each(responseJson, function(index, category) {
        $("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
      });
    });
  }

  function checkUnique(form) {
    ProductId = $("#id").val();
    ProductName = $("#name").val();

    csrfValue = $("input[name='_csrf']").val();

    params = {id: ProductId, name: ProductName, _csrf: csrfValue};

    $.post(checkUniqueUrl, params, function(response) {
      if (response == "OK") {
        form.submit();
      } else if (response == "Duplicate") {
        showModalDialog("Warning", "There is another brand having same name " + ProductName);
      } else {
        showModalDialog("Error", "Unknown response from server");
      }
    }).fail(function() {
      showModalDialog("Error", "Could not connect to the server");
    });

    return false;
  }