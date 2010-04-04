<?
$selected_menu_entry = 'home';

include_once('include/header.php.inc');
?>

    <div id="info1">
    	<div class="small">
		<span class="subHeader">Features:</span>
		<ul>
			<li>Multithreading</li>
			<li>Regular Expressions</li>
			<li>Save/Load download jobs</li>
			<li>Online Help</li>
			<li>HTTP/HTTPS supported</li>
			<li>HTTP Proxy Support</li>
			<li>HTTP Authentication</li>
			<li>Cookie Support</li>
			<li>Configurable User Agent</li>
			<li>Limitation of connections</li>
			<li>Configurable behaviour for HTTP response codes</li>
			<li>Bandwidth limitation</li>
			<li>GZip compression</li>
		</ul>
		</div>
    </div>
    
    <div id="main">
        <h3>About ItSucks</h3>
        <p>This project is a java web spider (web crawler) with the ability to download (and resume) files. It is also highly customizable with regular expressions and download templates.</p>
        <p>The application also provides a swing GUI and a console interface. All backend functionalities are also available in a separate library, they can be easily used for other projects.
        <br/><a class="small" href="about.php">[About]</a><a style="margin-left: 1.2em;" class="small" href="http://sourceforge.net/project/showfiles.php?group_id=186141.php">[Download]</a>
        </p>
        
    <div class="news">
        <h3>Version 0.4.0 is released</h3>
        <span class="subHeader">2009-11-26</span>
        <p>
		This release contains a complete rewrite of the resume logic. Resuming can be disabled over the GUI.<br/>
		New is also the support for referrals. Many sites use them to ensure you are not using deeplinks. (They can be disabled over the GUI though.)<br/>
		Also a issue is fixed with parameters in redirects (403). (Bug 2680409, thanks for the detailed bug report)
		</p>
		Other changes:
		<ul>
		<li>Switched to JAXB 2.0</li>
		<li>Updated Maven configuration (2.2)</li>
		<li>Refactoring HttpRetriever Interface</li>
		<li>Build source jars</li>
		</ul>		
	<p>
		Detailed changelog can be found here: <a href="https://sourceforge.net/projects/itsucks/files/itsucks/itsucks-releasenotes-0.4.0.txt/view"> ChangeLog 0.4.0</a>
	</p>
	</div>
        
		<a class="small" href="news.php">[previous releases]</a>
    </div>
    
<?
include_once('include/footer.php.inc');
?>
