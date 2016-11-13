$(function() {
	$('#userSearch').on('input', function() {
			$('input[name=artistOrLyrics]').val(""); // lancera une recherche des morceaux par lyrics
		});
		
		$('#userSearch').typeahead({
			source : function(query, process){
			    return $.ajax({
		            url : 'AutoArtistSearchServlet',
		            type : "GET",
		            dataType: "json",
		            data : { search : $("#userSearch").val() },
		            success : function( data ) {
		            	return process(data.artist);
	                }
		        });
		    },
		    updater:function (item) {
				$('input[name=artistOrLyrics]').val("artist"); // lancera une recherche des morceaux d'un artist
				$('#userSearch').val(item);
				$('#searchForm').submit();
		    }
    });
});