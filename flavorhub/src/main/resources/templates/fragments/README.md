# Recipe Card Component

A reusable Thymeleaf fragment for displaying recipe cards in the FlavorHub application.

## 📁 Location

- Fragment: `src/main/resources/templates/fragments/recipe-card.html`
- Demo Page: `src/main/resources/templates/recipe-card-demo.html`

## 🎯 Features

- ✅ Responsive design (mobile-first approach)
- ✅ Hover effects and smooth transitions
- ✅ Semantic HTML5 elements
- ✅ Full accessibility support (ARIA labels, alt text)
- ✅ Difficulty level badges (Easy/Medium/Hard) with color coding
- ✅ Recipe metadata display (prep time, cook time, servings)
- ✅ Optional image support with lazy loading
- ✅ Print-friendly styles
- ✅ Reduces motion for accessibility preferences

## 📖 Usage

### 1. Include the styles in your template's `<head>` section:

```html
<style th:replace="fragments/recipe-card :: recipe-card-styles"></style>
```

### 2. Use the fragment to display a single recipe:

```html
<div th:replace="fragments/recipe-card :: recipe-card(recipe=${recipe})"></div>
```

### 3. Display multiple recipes in a grid:

```html
<div class="recipes-grid">
  <div
    th:each="recipe : ${recipes}"
    th:replace="fragments/recipe-card :: recipe-card(recipe=${recipe})"
  ></div>
</div>
```

### Grid CSS (recommended):

```css
.recipes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 25px;
}
```

## 🎨 Recipe Model Requirements

The component expects a `Recipe` object with the following properties:

- `id` (Long) - Recipe identifier
- `name` (String) - Recipe name
- `cuisineType` (String) - Cuisine type (e.g., "Italian", "Mexican")
- `prepTime` (Integer) - Preparation time in minutes
- `cookTime` (Integer) - Cooking time in minutes
- `description` (String) - Recipe description
- `difficultyLevel` (String) - "Easy", "Medium", or "Hard"
- `servings` (Integer) - Number of servings
- `imageUrl` (String, optional) - Recipe image URL

## 🔧 Controller Example

```java
@Controller
public class RecipeViewController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping("/recipes")
    public String showRecipes(Model model) {
        List<Recipe> recipes = recipeService.getAllRecipes();
        model.addAttribute("recipes", recipes);
        return "recipes";
    }

    @GetMapping("/recipe/{id}")
    public String showRecipe(@PathVariable Long id, Model model) {
        Recipe recipe = recipeService.getRecipeById(id)
            .orElseThrow(() -> new RecipeNotFoundException(id));
        model.addAttribute("recipe", recipe);
        return "recipe-detail";
    }
}
```

## 🎨 Customization

### Difficulty Colors

The component automatically applies colored badges based on difficulty level:

- **Easy**: Green background (#d4edda) with dark green text (#155724)
- **Medium**: Yellow background (#fff3cd) with dark yellow text (#856404)
- **Hard**: Red background (#f8d7da) with dark red text (#721c24)

### Responsive Breakpoints

- **Desktop**: Full-size cards with all features
- **Tablet** (≤ 768px): Slightly smaller padding and font sizes
- **Mobile**: Optimized for small screens with adjusted spacing

## ♿ Accessibility Features

- Semantic HTML5 elements (`<article>`, proper heading hierarchy)
- ARIA labels for icons and metadata
- Alt text for images
- Keyboard navigation support
- Focus indicators
- Reduced motion support for users with motion sensitivity preferences
- High contrast color schemes for difficulty badges

## 🖨️ Print Styles

The component includes print-specific styles that:

- Remove shadows and hover effects
- Add borders for clarity
- Prevent page breaks inside cards
- Optimize layout for printing

## 📱 Browser Support

- Chrome/Edge (latest 2 versions)
- Firefox (latest 2 versions)
- Safari (latest 2 versions)
- Mobile browsers (iOS Safari, Chrome Mobile)

## 📝 Demo

To view the component in action, navigate to:

```
http://localhost:8080/recipe-card-demo.html
```

This demo page shows:

- Usage examples with code snippets
- Three sample recipe cards with different difficulty levels
- All styling variations

## 🔍 Testing

The component has been designed to work with:

- JUnit tests via `RecipeServiceTest`
- Manual testing in the demo page
- Integration with existing FlavorHub templates

## 🚀 Future Enhancements

Potential improvements for workshop participants:

- [ ] Add "favorite" button functionality
- [ ] Add click-to-view-details interaction
- [ ] Implement skeleton loading states
- [ ] Add recipe rating display
- [ ] Include dietary restriction badges
- [ ] Add animation on card appearance
- [ ] Implement card filtering animations

## 📚 Related Files

- [RecipeService.java](../java/com/coveros/training/flavorhub/service/RecipeService.java)
- [RecipeController.java](../java/com/coveros/training/flavorhub/controller/RecipeController.java)
- [Recipe.java](../java/com/coveros/training/flavorhub/model/Recipe.java)
- [recipes.html](../templates/recipes.html)

## 📖 Workshop Context

This component was created as part of the GitHub Copilot Advanced Workshop to demonstrate:

- Reusable Thymeleaf fragments
- Spring Boot template best practices
- Accessible and responsive web design
- Component-based architecture
