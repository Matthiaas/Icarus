//Cool pages to try out:
//www.bbc.co.uk
//blog.lenovo.com
//muenchen.de
//www.studentenwerk-muenchen.de



function onRequest(req, res) {
    //req.Path = req.Path.replace('-you-did-not-rtfm', '');
}

function onResponse(req, res) {
    //Replace stuff in text 
    //Some Memes
    //http://www.tiptopsigns.com/images/P/jdm_meme_lol.jpg
//http://i0.kym-cdn.com/entries/icons/mobile/000/000/091/TrollFace.jpg

    if (res.ContentType.indexOf("text/html") == 0) {
	var body = res.ReadBody();

	//Only replace image tags
        //body = body.replace(
        //    /(<img.*?)src=".+?"(.*?>)/gi,
        //    '$1 src="http://i.imgur.com/etjgJ2D.jpg" $2'
        //);
	
	//Replace source-attributes
	//body = body.replace(
        //    /src=".+?\.(jpg|jpeg|png|gif|bmp)"/gi,
        //    'src="http://i.imgur.com/etjgJ2D.jpg"'
        //);

	//Most aggressive: Replace every pair of "" with a image inside
	body = body.replace(
            /"+[^"]*(jpg|jpeg|png|gif|bmp)"/gi,
            '"http://i0.kym-cdn.com/entries/icons/mobile/000/000/091/TrollFace.jpg"'
        );

	//Patch Dickbutt texts in the webpage titles
	body = body.replace(
            /<title>(.*?)<\/title>/gi,
            '<title>PONRHUB - $1<\/title>'
        );

	
	log("Memed!" + req.Hostname + req.Path + ( req.Query ? "?" + req.Query : ''));

	//Replace random text in english or german language
	body = body.replace(/ the /gi, ' SCHMEKLES ');
	body = body.replace(/ be /gi, ' PLUMBUS ');
	body = body.replace(/ to /gi, ' DINGLEBOP ');
	body = body.replace(/ of /gi, ' SCHLEEM ');
	body = body.replace(/ and /gi, ' FLEEB ');

	body = body.replace(/ der /gi, ' SCHLAMI ');
	body = body.replace(/ die /gi, ' PLUMBUS ');
	body = body.replace(/ und /gi, ' SCHMEKLES ');
	body = body.replace(/ in /gi, ' PLOOBIS ');
	body = body.replace(/ den /gi, ' CHUMBLE ');
	
        
	res.Body = body;
    }

	//Replace all JPEGS by own
	else if (res.ContentType.indexOf("image/jpeg") != -1) {
		for (var i = 0; i < res.Headers.length; i++) {
		    res.RemoveHeader(res.Headers[i].Name);
		}
		res.SetHeader("Connection", "close");
		res.Status  = 200;
		res.Body    = readFile("caplets/www/troll.jpg");
		log("Replaced non http image! " + req.Hostname + req.Path + ( req.Query ? "?" + req.Query : ''));
	    }
}

