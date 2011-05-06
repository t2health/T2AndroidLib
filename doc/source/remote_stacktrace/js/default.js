$(document).ready(function() {
	// Handle result header input.
	$('.result .header').click(function(){
		$(this).parents('.result').find('.trace').toggle("slow");
	});
	
	$('.result .trace .delete').click(function() {
	    var dbid = $(this).parents('.result').attr('dbid');
	    var resEl = $(this).parents('.result');
	    
	    $.getJSON(
	      'callback.php?method=delete&id='+dbid, 
	      function(json) {
          $(resEl).find('.trace').hide('slow');
          $(resEl).hide('slow');
	      }
      );
	    
	    
	    return false;
	});
});
