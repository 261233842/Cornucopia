# Cornucopia and feeding mechanism renewal

## add Cornucopia

Cornucopia is a new food storage item that allows players to quickly and easily access its food resources. Now you can get a Cornucopia not only by joining a world, but also by using a goat horn item filled with food.

### How to get a Cornucopia：
- Automatically acquired when joining a new world. 
- Use a goat horn item filled with food to convert.

### Shortcut key：
- **Hold Ctrl + Shift + Q** at the same time: You can discard all the food in Cornucopia at once.

## Modification of goat horn item behavior

The original goat horn item can now be used to carry food, and when full, it can be converted into a cornucopia. This makes it easier for players to manage and eat the food they carry.

## Feeding mechanism

The feeding mechanism automatically selects the most appropriate food based on the player's health and hunger status. Here are the types of foods the system will choose in different situations:

- **Full hunger value**：When the player's hunger level is full, they will choose foods that can be eaten at full hunger level.
- **10 < health**：If health is greater than 10, preference is given to foods that will maximize hunger. In the case of the same overflow or insufficient hunger value, preference is given to the food that returns the most hunger value.
- **6 < health <= 10**：In this case, if the golden carrot is available, the golden carrot will be preferred for consumption.
- **health <= 6**：When health drops to 6 or lower, the golden Apple is checked for and preferred to restore health quickly. The preference between the two depends on the order in which they are placed. The one on the left of Cornucopia is preferred)

*The English version was translated by Youdao translator.*
---
# 丰饶角与进食机制更新

## 添加丰饶角

丰饶角是一个存储食物的新物品，它允许玩家快捷并方便地访问其中的食物资源。现在，您不仅可以通过加入一个世界获得丰饶角，还可以通过使用装满食物的山羊角物品来获得丰饶角。

### 获取丰饶角的方法：
- 加入一个新世界时自动获得。
- 使用装满食物的山羊角物品转换获得。

### 快捷键：
- **同时按住 Ctrl + Shift + Q**：可以将丰饶角中的所有食物一次性丢弃。

## 山羊角物品行为的修改

原有的山羊角物品现在可以被用来装载食物，当装满时，它可以转换为丰饶角。这使得玩家能够更便捷地管理和食用携带的食物。

## 进食机制

进食机制根据玩家的生命值和饥饿值状态自动选择最适宜的食物。以下是在不同情况下系统会选择的食物类型：

- **满饥饿值**：当玩家的饥饿值已满时，将选择那些可以在满饥饿值的情况下被食用的食物。
- **10 < 生命值**：如果生命值大于10点，会优先选择那些可以使饥饿值尽可能达到满值的食物。在溢出或不足的饥饿值相同的情况下，优先选择回复饥饿值最多的食物。
- **6 < 生命值 <= 10**：在这种情况下，如果有金胡萝卜，将优先选择金胡萝卜进行食用。
- **生命值 <= 6**：当生命值降至6点或更低时，将检查是否有（附魔）金苹果，并优先选择（附魔）金苹果以快速恢复生命值。（两者之间的选择优先级，取决于放入的顺序。丰饶角中靠左的优先吃）
