# 🍳 Recipe Card Component

A beautiful, responsive, and reusable recipe card component built with HTML, CSS, and Thymeleaf for the FlavorHub application.

## Features

✨ **Beautiful Design**

- Modern card layout with smooth hover effects
- Professional gradient backgrounds
- Clean typography and spacing

🎨 **Colorful Cuisine Badges**

- Unique gradient colors for each cuisine type
- Eye-catching visual badges positioned at the top-right
- 11 pre-configured cuisine styles (Italian, Mexican, Asian, Mediterranean, Indian, American, French, Japanese, Thai, Greek, Spanish, and Others)

📊 **Rich Metadata Display**

- Preparation time with emoji icon
- Cooking time with emoji icon
- Serving size with emoji icon
- Clear, organized layout

⭐ **Difficulty Levels**

- Three difficulty badge styles: Easy (green), Medium (yellow), Hard (red)
- Clear visual differentiation
- Color-coded for quick scanning

📱 **Responsive Design**

- Mobile-first approach
- Adapts gracefully to all screen sizes
- Touch-friendly interactive elements

♿ **Accessibility**

- ARIA labels for all interactive elements
- Proper semantic HTML structure
- Respects prefers-reduced-motion preference
- Color not the only indicator of information

🖨️ **Print-Friendly**

- Optimized print styles
- Professional appearance when printed
- Page break handling for multiple cards

## Recipe Model Fields

The component utilizes the following fields from the `Recipe` model:

| Field             | Type    | Example                    | Required |
| ----------------- | ------- | -------------------------- | -------- |
| `id`              | Long    | 1                          | Yes      |
| `name`            | String  | "Spaghetti Carbonara"      | Yes      |
| `description`     | String  | "Classic Italian pasta..." | No       |
| `imageUrl`        | String  | "/images/pasta.jpg"        | No       |
| `prepTime`        | Integer | 10                         | No       |
| `cookTime`        | Integer | 15                         | No       |
| `servings`        | Integer | 4                          | No       |
| `difficultyLevel` | String  | "Easy", "Medium", "Hard"   | No       |
| `cuisineType`     | String  | "Italian", "Mexican", etc. | No       |

## Cuisine Badge Colors

| Cuisine           | Gradient                       | Subheader Color |
| ----------------- | ------------------------------ | --------------- |
| **Italian**       | Red (#ff6b6b → #ee5a6f)        | White           |
| **Mexican**       | Gold (#f7b731 → #f5af19)       | Dark            |
| **Asian**         | Orange-Red (#ff6348 → #fd5e53) | White           |
| **Mediterranean** | Teal (#48dbfb → #1dd1a1)       | White           |
| **Indian**        | Orange (#ffa502 → #ff7675)     | White           |
| **American**      | Purple (#a29bfe → #6c5ce7)     | White           |
| **French**        | Gray (#dfe6e9 → #b2bec3)       | Dark            |
| **Japanese**      | Red (#ff7675 → #d63031)        | White           |
| **Thai**          | Orange (#ffa502 → #fdcb6e)     | Dark            |
| **Greek**         | Blue (#0984e3 → #74b9ff)       | White           |
| **Spanish**       | Red (#d63031 → #ff7675)        | White           |
| **Other**         | Gray (#636e72 → #b2bec3)       | White           |

## Usage

### 1. Include the Styles

In your Thymeleaf template's `<head>` section:

```html
<style th:replace="fragments/recipe-card :: recipe-card-styles"></style>
```

### 2. Display a Single Recipe

```html
<div th:replace="fragments/recipe-card :: recipe-card(recipe=${recipe})"></div>
```

### 3. Display Multiple Recipes in a Grid

```html
<div class="recipes-grid">
  <div
    th:each="recipe : ${recipes}"
    th:replace="fragments/recipe-card :: recipe-card(recipe=${recipe})"
  ></div>
</div>
```

### 4. Add Grid CSS (Optional)

If you want to display multiple cards in a responsive grid:

```css
.recipes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 30px;
  margin-top: 30px;
}
```

## Component Structure

### HTML Hierarchy

```
.recipe-card
├── .recipe-image-container (optional, if imageUrl exists)
│   ├── <img> .recipe-img
│   └── .recipe-image-overlay
├── .cuisine-badge-container
│   └── <span> .cuisine-badge
└── .recipe-content
    ├── .recipe-header
    │   └── <h3> .recipe-title
    ├── <p> .recipe-description
    ├── .recipe-meta
    │   ├── .meta-item (prep-time)
    │   ├── .meta-item (cook-time)
    │   └── .meta-item (servings)
    └── .recipe-footer
        └── <span> .difficulty-badge
```

## CSS Classes

### Main Classes

| Class                      | Purpose              | Notes                              |
| -------------------------- | -------------------- | ---------------------------------- |
| `.recipe-card`             | Main container       | Apply to wrapping div              |
| `.recipe-image-container`  | Image wrapper        | Optional, hidden if no image       |
| `.recipe-img`              | Image element        | Uses object-fit: cover             |
| `.cuisine-badge-container` | Badge wrapper        | Positioned absolute                |
| `.cuisine-badge`           | Cuisine badge        | Uses classappend for cuisine type  |
| `.recipe-content`          | Content wrapper      | Flexbox column layout              |
| `.recipe-title`            | Recipe name          | h3 element                         |
| `.recipe-description`      | Description text     | Clamped to 2 lines                 |
| `.recipe-meta`             | Metadata section     | Display: flex, 3 items             |
| `.meta-item`               | Individual metadata  | Flex item                          |
| `.meta-icon`               | Icon (emoji)         | 1.3em size                         |
| `.meta-content`            | Icon label & value   | Flexbox column                     |
| `.difficulty-badge`        | Difficulty indicator | Has .easy, .medium, .hard variants |

### Responsive Breakpoints

- **Desktop**: Full grid layout with large images (220px height)
- **Tablet**: Medium grid layout with medium images (200px height)
- **Mobile** (<768px): Single column, reduced padding, smaller images (180px height)

## Customization

### Modify Cuisine Badge Colors

Edit the `.cuisine-badge` color classes in the `recipe-card-styles`:

```css
.cuisine-badge.your-cuisine-type {
  background: linear-gradient(135deg, #color1 0%, #color2 100%);
  color: white; /* or #333 for light background */
}
```

### Change Grid Layout

Modify the `.recipes-grid` styles:

```css
.recipes-grid {
  grid-template-columns: repeat(2, 1fr); /* 2 cards per row */
  gap: 20px; /* Adjust spacing */
}
```

### Adjust Image Height

Modify `.recipe-image-container` height:

```css
.recipe-image-container {
  height: 250px; /* Increase from 220px */
}
```

## Browser Support

- Chrome/Edge: ✅ Full support
- Firefox: ✅ Full support
- Safari: ✅ Full support
- IE 11: ⚠️ No support (uses modern CSS features)

## Performance Optimizations

- Lazy loading images: `loading="lazy"` attribute
- Efficient CSS animations using `transform` and `opacity`
- No JavaScript dependencies
- Minimal CSS footprint
- Semantic HTML for optimal rendering

## Accessibility Features

1. **ARIA Labels**: All metadata items have descriptive aria-labels
2. **Semantic HTML**: Proper heading hierarchy and semantic elements
3. **Color Contrast**: Sufficient contrast ratios for readability
4. **Motion**: Respects `prefers-reduced-motion` preference
5. **Keyboard Navigation**: Cards are keyboard accessible
6. **Image Alt Text**: Descriptive alt text for images

## Demo Page

View the component in action at the [Recipe Card Demo](/recipe-card-demo) page, which shows:

- Multiple cuisine types with different badge colors
- All difficulty levels (Easy, Medium, Hard)
- Responsive behavior
- Interactive hover effects
- Usage examples and code snippets

## Example Data

Here's sample data for creating recipe objects:

```java
// Example Recipe in Java
Recipe spaghetti = new Recipe(
    "Spaghetti Carbonara",
    "Classic Italian pasta with creamy egg sauce, crispy bacon, and Pecorino cheese",
    10,  // prepTime
    15,  // cookTime
    4,   // servings
    "Easy",
    "Italian"
);
spaghetti.setImageUrl("/images/carbonara.jpg");
```

## Best Practices

1. ✅ Always provide `prepTime`, `cookTime`, and `servings` for complete metadata
2. ✅ Include recipe images for better visual appeal
3. ✅ Use proper `difficultyLevel` values: "Easy", "Medium", or "Hard"
4. ✅ Ensure cuisine type matches the color scheme list for proper styling
5. ✅ Keep descriptions concise (they're clamped to 2 lines)
6. ✅ Test on mobile devices to verify responsive behavior

## Troubleshooting

**Cards not displaying properly?**

- Ensure styles are included with `<style th:replace="...">`
- Check that recipe object is not null
- Verify Thymeleaf fragment syntax

**Images not showing?**

- Verify `imageUrl` is valid and accessible
- Check browser console for 404 errors
- Ensure proper image permissions

**Badges showing wrong colors?**

- Confirm `cuisineType` matches available options (case-insensitive)
- Check if custom styles override the defaults
- Clear browser cache

**Responsive issues?**

- Verify viewport meta tag: `<meta name="viewport" content="width=device-width, initial-scale=1.0">`
- Check that `.recipes-grid` CSS is properly applied
- Test with browser DevTools responsive mode

## Future Enhancements

- [ ] Add recipe rating/reviews display
- [ ] Include cost estimate
- [ ] Add dietary tags (vegan, gluten-free, etc.)
- [ ] Recipe difficulty in-line graphics
- [ ] Video recipe links
- [ ] Print recipe button

---

**Last Updated**: February 2026  
**Component Version**: 2.0  
**Framework**: Spring Boot 3.x + Thymeleaf 3.x
