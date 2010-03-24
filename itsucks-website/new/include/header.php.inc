<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<? 

	function selectMenuEntry($entry) {
		
		global $selected_menu_entry;
		
		if($entry == $selected_menu_entry) {
			echo 'current';
		} else {
			echo 'menu';
		} 
		
	}
	
?>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de" lang="de">
<head>
  <title>ItSucks</title>
  <meta name="language" content="en" />
  <meta http-equiv="Content-Type" content="text/html; charset=iso8859-15" />
  <link rel="stylesheet" type="text/css" href="itsucks.css" />
</head>

<body>
  <div id="page">
  <div id="header">
    <div id="headerImage">
      <img src="img/stormy.png" alt="Logo" />
    </div>
    <div id="downloadLatest">
    <p>
      Suck it NOW!<br/>
      Version 0.4.0
    </p>
    </div>
    
    
    <div id="headerText">
      <p id="titleText">ItSucks</p>
      <p id="titleText2">Welcome to the official homepage of ItSucks.</p>
    </div>
  </div>

  <div id="content">
    <div id="navigation"> <!-- start navigation -->
      <div>
        <h3>Main</h3>
      </div>
      <ul>
        <li><a class="<? selectMenuEntry('home'); ?>" href="index.php">Home</a></li>
        <li><a class="<? selectMenuEntry('news'); ?>" href="news.php">News</a></li>
        <li><a class="<? selectMenuEntry('about'); ?>" href="about.php">About</a></li>
        <li><a class="<? selectMenuEntry('development'); ?>" href="development.php">Development</a></li>
        <li><a class="<? selectMenuEntry('screenshots'); ?>" href="screenshots.php">Screenshots</a></li>
        <li><a class="<? selectMenuEntry('downloads'); ?>" href="http://sourceforge.net/project/showfiles.php?group_id=186141">Download</a></li>
      </ul>

      <div>
        <h3>Documentation</h3>
      </div>
      <ul>
      	<li><a class="<? selectMenuEntry('api'); ?>" href="apidocs/">Core API</a></li>
      	<li><a class="<? selectMenuEntry('examples'); ?>" href="examples.php">Examples</a></li>
      </ul>

      <div>
        <h3>Support</h3>
      </div>
      <ul>
        <li><a class="menu" href="http://sourceforge.net/forum/?group_id=186141">Forums</a></li>
        <li><a class="menu" href="http://sourceforge.net/tracker/?atid=916088&amp;group_id=186141&amp;func=browse">Bug Reports</a></li>
        <li><a class="menu" href="http://sourceforge.net/tracker/?atid=916091&amp;group_id=186141&amp;func=browse">Feature Requests</a></li>
      </ul>
    </div> <!-- end navigation -->
