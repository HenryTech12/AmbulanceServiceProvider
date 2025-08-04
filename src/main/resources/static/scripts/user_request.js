
    const apiKey = "AIzaSyCirh9WnsFgaaevtO96vkUSoJuH3kqjXAk"; // Use only in testing, not production

    let medical_report = document.getElementById('emergency');

    async function main() {
      const text = document.getElementById("emergency").value;
      console.log('Text: '+text);
      const response = await fetch(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${apiKey}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            contents: [
              {
                parts: [{ text: `the text contains a emergency description (which means the patient current state i want you to base the critical level on what the patient pass and not more than that just think on a plain level) Give me a critical level for: ${text} in just a word and shouldnt be more than one word and note if  you can't assess without data. just return invalid as the output` }],
              },
            ],
          }),
        }
      ).catch(err => console.log(err));


      const result = await response.json();
      const output = result.candidates?.[0]?.content?.parts?.[0]?.text || "No response";
      let medical_output = document.getElementById('medical_output');
      medical_output.value = output;
      console.log(output);
      //document.getElementById("output").textContent = output;
    }
  

medical_report.addEventListener('change',() => {
    main();
});


   