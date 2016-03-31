/**************************************************************************
 * Copyright (C) 2015 Poolweb - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 **************************************************************************/


$(document).ready(function(){

    /*
    $.fn.slideFadeToggle = function(speed, easing, callback) {
        return this.stop(true, false).animate({opacity: "toggle", height: "toggle"}, 100, "easeOutQuad", callback);
    };

    $(".topUserMenuLink").hover(function() {
        $(this).slideFadeToggle();
    });
    */

    /*
    $(".topMenuLink").hover(function() {
        $(this).stop(true, true).toggleClass("topMenuLinkHover", 100);
    });
    */




    // Mini buttons menu (toolbar)
    $("body").on("mouseover", ".miniBtn", function(){
        $(this).stop(false, true).addClass("hover", 300);
    });
    $("body").on("mouseleave", ".miniBtn", function(){
        $(this).stop(false, true).removeClass("hover", 100);
    });
    $("body").on("mouseover", ".miniBtnRow", function(){
        $(this).find(".miniBtnSpan").stop(false, true).removeClass("opacity10", 300);
    });
    $("body").on("mouseleave", ".miniBtnRow", function(){
        $(this).find(".miniBtnSpan").stop(false, true).addClass("opacity10", 100);
    });



    // Make p:messages disappear on close
    $("body").on("mouseover", ".ui-messages-close", function(){
        $(this).removeAttr("onclick");
        $(this).on("click", function(){
           $(this).parent().fadeOut(100);
        });
        return false;
    });


    // POPUP
    // Get source position and title for popup
    $("body").on("click", ".documentNo", function(){
        popupSrc = $(this);
        popupTitle = $(this).html() + " - Order detail";
    });

    // Close popup on click outside
    $("html").mousedown(function() {
        $(".ui-dialog").css({"display": "none"});
        $(".ui-widget-overlay").css({"display": "none"});
    });
    $(".ui-dialog").mousedown(function(event){
        event.stopPropagation();
    });



    // SPEECH BUBBLE
    // Render & position Speech Bubble + set content -> add "styleClass="dataBubble" pt:data-bubble="words to display in bubble"" to p:element
    $("body").on({
        mouseenter: function(){
            if($(this).data("bubble") != "" && !$(this).closest(".eventRow").hasClass("ui-draggable-dragging")) { //prevent bubble from appearing when no speech and when dragging
                if($(this).hasClass("eventCheckbox") && $(this).siblings(".scheduledNb").html() != 0) $("#bubble span").html($(this).siblings(".scheduledDates").html());   //set content: gets scheduledDates content if hover ext event checkbox
                else $("#bubble span").html($(this).data("bubble"));   //set content
                var posTop = $(this).offset().top - $("#bubble").outerHeight() - $("#bubble img").outerHeight() -13;    //get absolute top position
                $("#bubble").css({"top": posTop});    //set absolute top position

                if($(this).offset().left > $(window).width()/2) {
                    $("#bubble").addClass("right");
                    var bubbleWidth = $("#bubble").outerWidth();
                }
                else {
                    $("#bubble").removeClass("right");
                    var bubbleWidth = 0;
                }
                $(this).mousemove(function(event) {   //get & set absolute left position following mouse
                    var posLeft = event.pageX +1 -bubbleWidth;
                    $("#bubble").css({"left": posLeft});
                });
                $("#bubble").css("display", "inline-block");    //make Bubble appear...
                $("#bubble").stop(false, false).animate({"top": "+=8", "opacity": 1}, 100);  //... with animation
            }
        },
        mouseleave: function(){ //make Bubble disappear with animation
            $("#bubble").stop(false, false).animate({"top": "-=8", "opacity": 0}, 100, function() { $("#bubble").css("display", "none") });  //... with animation
        }
    }, ".dataBubble");

    $("body").on({
        mouseleave: function(){ //make Bubble disappear with animation
            $(this).stop(false, false).animate({"opacity": 0}, 100, function() { $(this).css("display", "none") });  //... with animation
        }
    }, ".scheduledDates");



    // Allow only numbers on text inputs with style class "numberOnly" -> Chrome not compatible with keycodes 51 & 52 ("3", "4"): gets keycode 222 instead
    /*
    $("body").on("keydown", ".numberOnly", function(e){
        //46, 8, 9, 27, 13, 109 = delete, backspace, tab, escape, enter, subtract
        -1!==$.inArray(e.keyCode, [46,8,9,27,13,109])||/65|67|86|88/.test(e.keyCode)&&(!0===e.ctrlKey||!0===e.metaKey)||35<=e.keyCode&&40>=e.keyCode||(e.shiftKey||48>e.keyCode||57<e.keyCode)&&(96>e.keyCode||105<e.keyCode)&&e.preventDefault()
    });
    */


    // Highlight table row on input focus
    $("body").on("focus blur", ".productTable input[type=text]", function() {
        $(this).closest("tr").toggleClass("highlightRow");
    });



    // When click on any SelectOneMenu: add placeholder with text "Search" to every filter input
    $("body").on("click", ".ui-selectonemenu", function() {
        $(".ui-selectonemenu-filter").attr("placeholder", "Search");
    });



    /* FULL CALENDAR */

    // Sort external events by status
    //var sortEvents = $.merge($.merge($("li.Assign.true").get(), $("li.Assign.false").get()), $("li.Capture").get());
    //$(".ui-datalist-data").html(sortEvents);


    // Call function initializing the draggable ability of external events
    initDraggable();


		// Initialize the calendar
    $("#calendar").fullCalendar({
        header: {
            left: "title",
            center: "prev, next",
            right: "month, agendaDay"
        },
        dayClick: function(date, jsEvent, view) { //toggle view on day click
            if(view.name == "month") {
                $("#calendar").fullCalendar("changeView", "agendaDay");
                $("#calendar").fullCalendar("gotoDate", date);
            }
        },
        aspectRatio: 0,
        allDayText: "Temp",
        minTime: "07:00",
        maxTime: "18:00",
        timeFormat: "h:mm a",
        axisFormat: "h a",
        //timeZone: "GMT-7",
        slotLabelInterval: "01:00",
        slotDuration: "00:15",
        dragRevertDuration: 0,  //0 prevents event from going back to its original position when canceled
        eventOverlap: true,
        eventConstraint: {
            start: today,
            end: endDay,
            //dow: [ 1, 2, 3, 4, 5, 6 ] //no drop event on Sundays (0=Sunday)
        },
        allDaySlot: true,
        editable: true,
        selectable: false,
        droppable: true, //allow things to be dropped onto the calendar
        drop: function(date, jsEvent) {
             //$(this).remove();  //remove cloned object
             $(".ui-draggable-dragging").remove();  //remove clone
             $(this).addClass("scheduled");     //external event is visibly (css) scheduled, disabled...
             //$(this).draggable("option", "disabled", true); //...and is no more draggable
             $("#calendar").fullCalendar("changeView", "agendaDay");
             $("#calendar").fullCalendar("gotoDate", date);
        },
        events: function(start, end, timezone, callback) {
        	  if ($("#hidden").val() == '') return;
            var data = {};
            data.action = "events";
            var myevent = {};
            data.resourceId = $("#hidden").val();
            data.event = myevent;
            $.ajax({
                url: "scheduler",
                type: "POST",
                contentType: "application/json",
                mimeType: "application/json",
                data: JSON.stringify(data),
                success: function (data) {
                    var events = [];
                    $(data.events).each(function(key, event) {
                        events.push({
                            orderId: event.orderId,
                        	id: event.id,
                            title: event.title,
                            start: event.startDate,
                            end: event.endDate,
                            address: event.address,
                            workedTime: event.workedTime,
                            freeTime: event.freeTime,
                            dayEvent: event.dayEvent,
                            //backgroundColor: event.backgroundColor,
                            description: event.description,
                            allDay: event.allDay,
                            confirmed: event.confirmed
                        });
                    });
                    callback(events);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert(errorThrown);
                },
                complete: function () {
                }
            });
        },
        viewRender: function(view, element) {
            if(view.name == "month") $("#calendar").fullCalendar("refetchEvents");
        },
        eventResize: function(event, delta, revertFunc) {
            updateEvent(event, "updateEvent");
        },
        eventDrop: function(event, dayDelta, minuteDelta, allDay, revertFunc) {
            updateEvent(event, "updateEvent");
        },
        eventReceive: function(event) {
       	    updateEvent(event, "createEvent");
        },
        eventDragStop: function(event, jsEvent) { //unschedule event on drag/drop over external events list
            var trashEl = jQuery("#calendarTrash");
            var ofs = trashEl.offset();
            var x1 = ofs.left;
            var x2 = ofs.left + trashEl.outerWidth(true);
            var y1 = ofs.top;
            var y2 = ofs.top + trashEl.outerHeight(true);
            if (jsEvent.pageX >= x1 && jsEvent.pageX<= x2 && jsEvent.pageY>= y1 && jsEvent.pageY <= y2) {
                updateEvent(event, "deleteEvent");
                $(this).remove();
            }
        },
        eventRender: function(event, element, view) {
            if(view.name == "month") {
                loadChart(event.workedTime, event.freeTime, event.start.format("YYYY-M-D"));
                $(".lbl"+event.start.format("YYYY-M-D")).html(event.dayEvent);
                $("#chart-area"+event.start.format("YYYY-M-D")).parent().removeClass("opacityNull"); //render current event's chart
            }
            element.addTouch();
        },
        dayRender: function(date, cell) {
            if($("#calendar").fullCalendar("getView").name == "month") {
                cell.html('<div id="chartDiv"><canvas id="chart-area'+date.format("YYYY-M-D")+'" class="dayChart"/><span class="chartLbl lbl'+date.format("YYYY-M-D")+'"></span></div>');
                date = date.format("L");
                if(convertDateToInt(date) >= convertDateToInt(endDay)) cell.addClass("fc-distant-future");
            }
        }
    });
    /* FULL CALENDAR */

});


// Set popup position and title
function popupPosition(topGap, leftGap) {
    var topPos = popupSrc.offset().top + topGap;
    var leftPos = popupSrc.offset().left + leftGap;
    $(".orderDetail").css({"top": topPos, "left": leftPos, "width": $("#calendarTrash").outerWidth()-(leftPos-$("#calendarTrash").offset().left)});
    $(".ui-dialog-title").html(popupTitle);

    // Convert sizes in decimal inches to fractional inches
    $(".shutterSize").each(function(index) {
        var sizeValue = parseFloat($(this).html()).toFixed(3);
        var inchSize = sizeValue.split('.'); //array [int, decimal]
        if(inchSize[1] != "000") {
            inchSize.push(decimalToFraction(inchSize[1]));
            var fraction = inchSize[2].split("/");
            $(this).html(inchSize[0]+" <span class='sup'>"+fraction[0]+"</span><span class='slash'>/</span><span class='sub'>"+fraction[1]+"</span>''");
        }
        else $(this).append("''");
    });
}



function convertDateToInt(date) {
    date = parseInt(date.substr(6, 4) + date.substr(0, 2) + date.substr(3, 2), 10);
    return date;
}



function decimalToFraction(inchSize){
    for(i in inchesFraction){
        if(inchSize == inchesFraction[i].decimal) return inchesFraction[i].fraction;
    }
}



// in Installation page, change confirm btn name according to active tab
function changeConfirmBtn(index) {
    index++;
    activeTabName = "Confirm "+ $("li.ui-state-default:nth-child("+ index +") a").html().toLowerCase();
    $(".scheduleBtn .ui-button-text").html(activeTabName);
}



// Get events to be sent to Gmap
function getCurrentEvents() {
    var eventsArray = $("#calendar").fullCalendar("clientEvents");
    var currentDate = $("#calendar").fullCalendar("getDate");
    currentDate = currentDate.format("L");
    var wp = [];
    var wpInfoWindow = [];
    eventsArray.sort(function(a, b){ return a.start-b.start }); // Sort events by date
    for(i in eventsArray){
        date = eventsArray[i].start.format("L");
        if(convertDateToInt(date) == convertDateToInt(currentDate)) {
            wp.push({location: eventsArray[i].address});   //set all items as waypoints
            var phone = eventsArray[i].title.slice(eventsArray[i].title.indexOf(","), eventsArray[i].title.lastIndexOf("\n"));  //extract phone number from title
            var title = eventsArray[i].title.slice(0, eventsArray[i].title.lastIndexOf("\n")).replace("\n", "<br/>").replace(phone, "");  //extract SO# and client name from title
            phone = phone.replace(", ", "");
            wpInfoWindow.push({title: title, phone: phone});   //set all items as waypoints
        }
    }
    var origin = {location: wp[0].location, title: wpInfoWindow[0].title, phone: wpInfoWindow[0].phone};  //set first item as origin
    var destination = {location: wp[wp.length-1].location, title: wpInfoWindow[wpInfoWindow.length-1].title, phone: wpInfoWindow[wpInfoWindow.length-1].phone};  //set last item as destination
    wp.splice(wp.length-1, 1);  //remove last item =destination (already extracted)
    wpInfoWindow.splice(wpInfoWindow.length-1, 1);
    wp.splice(0, 1);  //remove first item =origin (already extracted)
    wpInfoWindow.splice(0, 1);
    calculateAndDisplayRoute(directionsService, directionsDisplay, origin, destination, wp, wpInfoWindow);
}


function loadingBtn(thisBtn, btnTxt, disabled) {
    if(disabled) {  //prevent double click (double submit)
        $("."+thisBtn).attr("disabled", "disabled");
        $("."+thisBtn).addClass("processingBtn");
        $("body").css("cursor", "wait");
    }
    else {  //re-enable btn when done
        $("."+thisBtn).removeAttr("disabled");
        $("."+thisBtn).removeClass("processingBtn");
        $("body").css("cursor", "default");
    }
    $("."+thisBtn).children(".ui-icon").toggleClass("fa-loading");
    $("."+thisBtn).children(".ui-button-text").html(btnTxt);
}


function loadChart(workedTime, availableTime, eventId) {
    var data = [
        {
          value: workedTime,
          color: "#ff596d"  //"#885fff"
        },
        {
          value: availableTime,
          color: "#ffe2e5"  //"#e7dfff"
        }
    ];
    var ctx = document.getElementById("chart-area"+eventId).getContext("2d");
    new Chart(ctx).Doughnut(data, {animationEasing: "easeOutQuad", animationSteps: 30, percentageInnerCutout: 70, segmentShowStroke: false, showTooltips: false});
}


function reloadCalendar(resourceId) {
    $("#hidden").val(resourceId);
    $("#calendar").fullCalendar("refetchEvents");
    $("#calendar").fullCalendar("changeView", "month");
    $("#scheduler").removeClass("invisible"); //render calendar div
    $("#scheduler").stop(true, false).animate({"opacity": 1}, 140, function() {
          $("#calendarTrash").removeClass("invisible");   //render events div
          $("#calendarTrash").stop(true, false).animate({"opacity": 1}, 140, function() {
                $(".mapDiv").removeClass("invisible");
                $(".mapDiv").stop(true, false).animate({"opacity": 1}, 140);
          });
    });
}


// Initialize the external events
var revertDuration = 200;
function initDraggable() {
    $(".eventDrag").each(function() {
        $(this).data("event", {  //store data
            orderId: $(this).find(".orderId").val(),
            title: "SO #" + $(this).find(".documentNo").text() +" R#"+$(this).find(".reqDocumentNo").text() + "\n" + $(this).find(".bpName").text() + ", " + $(this).find(".phone").text() + "\n" + $(this).find(".city").text(), //set element's text as the event title
            address: $(this).find(".address").text()
        });

        if(!$(this).hasClass("Verify")) {  //prevent Red external events (not paid) from being dragged
            $(this).draggable({  //make the event draggable using jQuery UI
                zIndex: 999,
                revert: true,    //event goes back to its original position after drop
                revertDuration: revertDuration,
                appendTo: "body",
                helper: "clone",
                start: function(event, ui) {
                    $(".ui-draggable-dragging").css({"width": $(this).width()});  //set draggable clone's width
                    $(this).css({"opacity": .1}); //make cloned object disappear on drag
                    $(".scheduledDates").css({"display": "none"}); //make scheduledDates disappear on drag
                },
                revert: function(valid) {  //make cloned object reappear after clone being illegally dropped & reverted
                    var clonedObj = $(this);
                    if(!valid) {
                        setTimeout(function(){
                            clonedObj.css({"opacity": 1});
                        }, revertDuration);
                    }
                    return !valid;
                }
            });
            $(this).addTouch();
        }
    });
}


function updateEvent(event, updateEvent) {
    var data = {};
    data.action = updateEvent;
    data.resourceId = $("#hidden").val();
    var myevent = {};
    myevent.orderId = event.orderId;
    myevent.id = event.id;
    myevent.title = event.title;
    myevent.description = event.description;
    myevent.address = event.address;
    myevent.confirmed = event.confirmed;
    myevent.startDate = event.start;
    if (event.end !== undefined) myevent.endDate = event.end;
    myevent.dayDelta = event.dayDelta;
    myevent.minuteDelta = event.dayDelta;
    myevent.allDay = event.allDay;
    data.event = myevent;

    console.log(updateEvent+"====="+JSON.stringify(myevent));
    $.ajax({
        url: "scheduler",
        type: "POST",
        contentType: "application/json",
        mimeType: "application/json",
        data: JSON.stringify(data),
        success: function (data) {
            event.id = data.id;
            if(updateEvent == "deleteEvent" || updateEvent == "createEvent") rc([{name: "myOrderId", value: myevent.orderId}]);
            if(updateEvent == "deleteEvent") $("#calendar").fullCalendar("removeEvents", event._id);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        }
    });
}





// Initialize variables
var popupSrc;
var popupTitle;
var m = new moment();
var today = m.format("L");
var endDay = m.add(61, "days").calendar();
var activeTabName;
var inchesFraction = [{fraction: "1/8", decimal: "125"},{fraction: "1/4", decimal: "250"},{fraction: "3/8", decimal: "375"},{fraction: "1/2", decimal: "500"},{fraction: "5/8", decimal: "625"},{fraction: "3/4", decimal: "750"},{fraction: "7/8", decimal: "875"},{fraction: "1/16", decimal: "0625"},{fraction: "3/16", decimal: "1875"},{fraction: "5/16", decimal: "3125"},{fraction: "7/16", decimal: "4375"},{fraction: "9/16", decimal: "5625"},{fraction: "11/16", decimal: "6875"},{fraction: "13/16", decimal: "8125"},{fraction: "15/16", decimal: "9375"},{fraction: "1/32", decimal: "03125"},{fraction: "3/32", decimal: "09375"},{fraction: "5/32", decimal: "15625"},{fraction: "7/32", decimal: "21875"},{fraction: "9/32", decimal: "28125"},{fraction: "11/32", decimal: "34375"},{fraction: "13/32", decimal: "40625"},{fraction: "15/32", decimal: "46875"},{fraction: "17/32", decimal: "53125"},{fraction: "19/32", decimal: "59375"},{fraction: "21/32", decimal: "65625"},{fraction: "23/32", decimal: "71875"},{fraction: "25/32", decimal: "78125"},{fraction: "27/32", decimal: "84375"},{fraction: "29/32", decimal: "90625"},{fraction: "31/32", decimal: "96875"},{fraction: "1/64", decimal: "015625"},{fraction: "3/64", decimal: "046875"},{fraction: "5/64", decimal: "078125"},{fraction: "7/64", decimal: "109375"},{fraction: "9/64", decimal: "140625"},{fraction: "11/64", decimal: "171875"},{fraction: "13/64", decimal: "203125"},{fraction: "15/64", decimal: "234375"},{fraction: "17/64", decimal: "265625"},{fraction: "19/64", decimal: "296875"},{fraction: "21/64", decimal: "328125"},{fraction: "23/64", decimal: "359375"},{fraction: "25/64", decimal: "390625"},{fraction: "27/64", decimal: "421875"},{fraction: "29/64", decimal: "453125"},{fraction: "31/64", decimal: "484375"},{fraction: "33/64", decimal: "515625"},{fraction: "35/64", decimal: "546875"},{fraction: "37/64", decimal: "578125"},{fraction: "39/64", decimal: "609375"},{fraction: "41/64", decimal: "640625"},{fraction: "43/64", decimal: "671875"},{fraction: "45/64", decimal: "703125"},{fraction: "47/64", decimal: "734375"},{fraction: "49/64", decimal: "765625"},{fraction: "51/64", decimal: "796875"},{fraction: "53/64", decimal: "828125"},{fraction: "55/64", decimal: "859375"},{fraction: "57/64", decimal: "890625"},{fraction: "59/64", decimal: "921875"},{fraction: "61/64", decimal: "953125"},{fraction: "63/64", decimal: "984375"}];
