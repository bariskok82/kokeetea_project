$(function () {
    "use strict";

    var didscroll = false;
    $(window).scroll(function(){
        didscroll = true;
    });
    $(window).resize(function(){
        didscroll = true;
    });

    setInterval(function () {
        if (didscroll) {
          didscroll = false;
          var width = (window.innerWidth > 0) ? window.innerWidth : this.screen.width;
      
          if ($(window).scrollTop() > 0 && width>=768) {
            $(".navbar-nav").css("opacity", 0.4);
            $("#topbar-ribbon").css("height", "20px");
          } else {
            $(".navbar-nav").css("opacity", 1);
            $("#topbar-ribbon").css("height", "80px");
          }
        }
      }, 100);

    // Feather Icon Init Js
    feather.replace();

    $(".preloader").fadeOut();

    // this is for close icon when navigation open in mobile view
    $(".nav-toggler").on('click', function () {
        $("#main-wrapper").toggleClass("show-sidebar");
        $(".nav-toggler i").toggleClass("ti-menu");
    });

    // ==============================================================
    //tooltip
    // ==============================================================
    $(function () {
        $('[data-toggle="tooltip"]').tooltip()
    })
    // ==============================================================
    //Popover
    // ==============================================================
    $(function () {
        $('[data-toggle="popover"]').popover()
    })

    // ==============================================================
    // Perfact scrollbar
    // ==============================================================


    // ==============================================================
    // Resize all elements
    // ==============================================================
    $("body, .page-wrapper").trigger("resize");
    $(".page-wrapper").delay(20).show();

    // For Custom File Input
    $('.custom-file-input').on('change', function () {
        //get the file name
        var fileName = $(this).val();
        //replace the "Choose a file" label
        $(this).next('.custom-file-label').html(fileName);
    })
});