// Initialize CodeMirror
const editor = CodeMirror.fromTextArea(document.getElementById('editor'), {
  mode: 'groovy',
  theme: 'monokai',
  lineNumbers: true,
  autoCloseBrackets: true,
  matchBrackets: true,
  indentUnit: 4,
  tabSize: 4,
  lineWrapping: true,
  foldGutter: true,
  gutters: ['CodeMirror-linenumbers'],
  extraKeys: {
    'Ctrl-Space': 'autocomplete',
  },
});

// Set some sample code
editor.setValue(`// Example Groovy code
def greeting = "Hello, World!"
println greeting

def numbers = [1, 2, 3, 4, 5]
numbers.each { num ->
    println "Number: $num"
}`);

// Handle Run button click
document.getElementById('runButton').addEventListener('click', async () => {
  const code = editor.getValue();
  const outputElement = document.getElementById('output');
  const runButton = document.getElementById('runButton');

  try {
    // Disable the run button while executing
    runButton.disabled = true;
    runButton.textContent = 'Running...';
    outputElement.textContent = 'Executing code...';

    const response = await fetch('https://api.jdoodle.com/v1/execute', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        clientId: '9de365557beea17ff94b36f30bf24244', // Replace with your JDoodle client ID
        clientSecret:
          'a6675a4ce4bbfc3a1326bc569bb1e989c968f9af22d197e91c6ea2d5dc454267', // Replace with your JDoodle API key
        script: code,
        language: 'groovy',
        versionIndex: '3',
      }),
    });

    const data = await response.json();

    if (data.output) {
      outputElement.textContent = data.output;
    } else if (data.error) {
      outputElement.textContent = `Error: ${data.error}`;
    } else {
      outputElement.textContent = 'No output received from the execution.';
    }
  } catch (error) {
    outputElement.textContent = `Error: ${error.message}`;
  } finally {
    // Re-enable the run button
    runButton.disabled = false;
    runButton.textContent = 'Run Code';
  }
});
