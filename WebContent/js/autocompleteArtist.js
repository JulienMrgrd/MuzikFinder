$(function() {
		$('#userSearch').on('keypress', function (e) {
	         if(e.which === 13){
	        	$('input[name=isArtist]').attr('disabled', true);
	 			$('#searchForm').submit();
	         }
	   });
	
		var artists = new Bloodhound({
		  datumTokenizer: Bloodhound.tokenizers.whitespace,
		  queryTokenizer: Bloodhound.tokenizers.whitespace,		  
		  remote:{ 
			  url: 'AutoArtistSearchServlet?search=%QUERY', 
			  wildcard: '%QUERY',
		  }

		});
		artists.initialize();
		 
		$('.typeahead').typeahead(null,
		{
		  name: 'artists',
		  source: artists.ttAdapter()
		}).on('typeahead:selected',function(evt,datum){ // artist selectionné
			console.log(datum);
			$('input[name=isArtist]').attr('disabled', false);
			$('input[name=isArtist]').val("true");
			$('#searchForm').submit();
		});	    
	
});