<?
$selected_menu_entry = 'development';

include_once('include/header.php.inc');
?>

    <div id="main">
      <p class="text">
      
        <h2>Develop with ItSucks</h2>
        
        <h3>Requirements</h3>
        <p>
        	To build and use ItSucks you need Java 1.6, Maven2 and an Subversion client.
        </p>
        
        <h3>Load ItSucks from SVN repository</h3>
        <p>
        	First load the latest stable version from the sourceforge subversion repository. 
        	This command loads the latest stable version of ItSucks and place it in an directory 'itsucks'.
        	<div class="code">
        		svn co https://itsucks.svn.sourceforge.net/svnroot/itsucks/tags/stable itsucks  
        	</div>
        </p>

        <h3>Build ItSucks</h3>
        <p>
        	To compile and install ItSucks in your maven2 repository execute the following commands:
        	<div class="code">
        		cd itsucks/itsucks-parent<br>
        		mvn -D maven.test.skip=true -P core install  
        	</div>
            Note: This will compile and install only the core libraries from ItSucks.
        </p>

        <h3>Run the example project</h3>
        <p> 
        	To compile and run the example project, execute the following commands.
        	<div class="code">
        		cd ../itsucks-example<br>
        		mvn compile<br>
        		mvn exec:java  
        	</div>
        </p>
        
        <h3>Include ItSucks in your project</h3>
        <p>
        	Now that the ItSucks libraries are available, you can use them 
        	by adding the following dependency to your maven2 pom.xml
        	 
        	<div class="code">
        	    &lt;dependency&gt;<br>
			      &lt;groupId&gt;itsucks&lt;/groupId&gt;<br>
      			  &lt;artifactId&gt;itsucks-core&lt;/artifactId&gt;<br>
			      &lt;version&gt;0.4.0&lt;/version&gt;<br>
    			&lt;/dependency&gt;<br>
        	</div>
        </p>        
        
      </p>    
    </div>
<?
include_once('include/footer.php.inc');
?>
